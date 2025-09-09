package ru.practicum.shareit.item.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.service.BookingAggregatorService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Validated
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingAggregatorService bookingAggregatorService;
    private final CommentAggregatorService commentAggregatorService;
    private final ItemMapper itemMapper;
    private final CommentService commentService;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        userService.getById(userId);

        Item item = itemMapper.mapToEntity(itemDto);
        item.setOwnerId(userId);

        itemRepository.save(item);
        return itemMapper.mapToDto(item);
    }

    @Override
    @Transactional
    public @Valid ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Товар не найден"));

        if (!userId.equals(item.getOwnerId())) {
            throw new ItemNotFoundException(
                    "Товар с id:" + itemId + " не принадлежит пользователю с id:" + userId
            );
        }

        applyUpdates(item, itemDto);
        return itemMapper.mapToDto(item);
    }

    @Override
    public ItemDetailsDto getById(Long itemId, Long userId) {
        userService.getById(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Товар не найден"));

        ItemDetailsDto dto = bookingAggregatorService.addBookings(List.of(item)).get(0);
        dto = commentAggregatorService.addComments(dto);

        if (!userId.equals(item.getOwnerId())) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        }
        return dto;
    }

    @Override
    public List<ItemDetailsDto> getAllByOwner(Long ownerId) {
        userService.getById(ownerId);

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        List<ItemDetailsDto> itemsWithBookings = bookingAggregatorService.addBookings(items);
        return commentAggregatorService.addComments(itemsWithBookings);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemMapper.mapToDtoList(itemRepository.findAvailableByText(text.toLowerCase()));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        return commentService.addComment(userId, itemId, commentDto);
    }

    private void applyUpdates(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }
}
