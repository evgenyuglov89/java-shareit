package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@AllArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestClient requestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> findAll() {
        return requestClient.findAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findAllByRequestId(@PathVariable Long requestId) {
        return requestClient.findAllByRequestId(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.findAllByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody RequestDto dto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.save(dto, userId);
    }
}
