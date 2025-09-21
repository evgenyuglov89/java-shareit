package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long requestorId,
            @RequestBody ItemRequestCreateDto newItemRequest) {

        ItemRequestDto itemRequestDto = itemRequestService.addItemRequest(requestorId, newItemRequest);
        return ResponseEntity.ok(itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestExtendedDto>> getUserItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long requestorId) {

        Collection<ItemRequestExtendedDto> result = itemRequestService.getUserItemRequests(requestorId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> getItemRequests() {
        Collection<ItemRequestDto> result = itemRequestService.getItemRequests();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestExtendedDto> getItemRequest(
            @PathVariable(name = "requestId") Long requestId) {

        ItemRequestExtendedDto result = itemRequestService.getItemRequest(requestId);
        return ResponseEntity.ok(result);
    }
}
