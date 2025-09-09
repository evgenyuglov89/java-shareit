package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto create(long userId, BookingCreateDto dto);

    BookingDto updateStatus(long bookingId, boolean approved, long userId);

    BookingDto getById(long bookingId, long userId);

    List<BookingDto> getAllByBooker(BookingState state, long userId);

    List<BookingDto> getAllByOwner(BookingState state, long userId);
}
