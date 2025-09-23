package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemClient.getAllByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @PathVariable Long itemId,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId
    ) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByName(@RequestParam String text) {
        return itemClient.getAllByName(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.create(dto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable Long itemId,
            @RequestBody ItemDto dto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.update(itemId, dto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestBody CommentCreateDto comment,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemClient.addComment(comment, itemId, userId);
    }
}
