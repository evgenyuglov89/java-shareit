package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto create(ItemDto dto, Long ownerId) {
        UserDto owner = userService.getById(ownerId);

        if (owner == null) {
            throw new UserNotFoundException("Владелец с id " + ownerId + " не найден");
        }

        Item item = ItemMapper.toItem(dto, new User(ownerId, null, null), null);
        item.setId(idGenerator.getAndIncrement());
        storage.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto dto, Long ownerId) {
        Item item = storage.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Товар не найден");
        }

        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new UserNotFoundException("Только владелец может редактировать товар");
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        item.setAvailable(dto.getAvailable());

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = storage.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Товар не найден");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return storage.values().stream()
                .filter(i -> i.getOwner().getId().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lower = text.toLowerCase();

        return storage.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()) && item.getName() != null)
                .filter(item -> item.getName().toLowerCase().contains(lower))
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
