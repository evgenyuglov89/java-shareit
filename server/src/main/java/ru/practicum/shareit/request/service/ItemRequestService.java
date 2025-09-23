package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long requestorId, ItemRequestCreateDto newItemRequest);

    Collection<ItemRequestExtendedDto> getUserItemRequests(Long userId);

    Collection<ItemRequestDto> getItemRequests();

    ItemRequestExtendedDto getItemRequest(Long requestId);

}
