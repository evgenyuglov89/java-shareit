package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.create(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@Valid @PathVariable Long itemId,
                                          @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.update(itemId, itemDto, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getById(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getAllByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        return ResponseEntity.ok(itemService.search(text));
    }
}
