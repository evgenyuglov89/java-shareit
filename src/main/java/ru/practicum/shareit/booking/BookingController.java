package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid BookingCreateDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@PathVariable long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.updateStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllByBooker(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllByOwner(state, userId);
    }
}
