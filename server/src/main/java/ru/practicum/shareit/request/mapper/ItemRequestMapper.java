package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestor().getId())
                .build();
    }

    public ItemRequestExtendedDto mapToItemRequestExtendedDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemDto> itemsDto = (items == null)
                ? List.of()
                : items.stream()
                .map(itemMapper::mapToDto)
                .collect(Collectors.toList());

        return ItemRequestExtendedDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestor().getId())
                .items(itemsDto)
                .build();
    }
}
