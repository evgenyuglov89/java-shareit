package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDto addItemRequest(Long requestorId, ItemRequestCreateDto newItemRequest) {
        User requestor = getUserOrThrow(requestorId);

        ItemRequest itemRequest = createItemRequest(newItemRequest, requestor);

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.mapToItemRequestDto(savedRequest);
    }

    @Override
    public Collection<ItemRequestExtendedDto> getUserItemRequests(Long userId) {
        getUserOrThrow(userId);

        List<ItemRequest> userItemRequests = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId);

        Map<Long, List<Item>> itemsByRequestId = getItemsGroupedByRequestId(userItemRequests);

        return userItemRequests.stream()
                .map(req -> itemRequestMapper.mapToItemRequestExtendedDto(
                        req,
                        itemsByRequestId.getOrDefault(req.getId(), List.of())
                ))
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getItemRequests() {
        return itemRequestRepository.findAllByOrderByCreatedDesc().stream()
                .map(itemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestExtendedDto getItemRequest(Long requestId) {
        ItemRequest itemRequest = getItemRequestOrThrow(requestId);
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return itemRequestMapper.mapToItemRequestExtendedDto(itemRequest, items);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден с id: " + userId));
    }

    private ItemRequest createItemRequest(ItemRequestCreateDto dto, User requestor) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    private ItemRequest getItemRequestOrThrow(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(
                        "Не найден запрос на добавление вещи с id: " + requestId));
    }

    private Map<Long, List<Item>> getItemsGroupedByRequestId(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        return itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));
    }
}
