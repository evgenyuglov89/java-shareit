package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookingAggregatorService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;

    public List<ItemDetailsDto> addBookings(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        List<BookingDtoForItem> bookings = bookingRepository.findAllByItemIdIn(itemIds).stream()
                .filter(b -> !b.getStatus().equals(BookingStatus.REJECTED))
                .map(bookingMapper::mapToItemBookingDto)
                .toList();

        return items.stream()
                .map(item -> enrichWithBookings(item, bookings))
                .toList();
    }

    private ItemDetailsDto enrichWithBookings(Item item, List<BookingDtoForItem> bookings) {
        ItemDetailsDto dto = itemMapper.mapToDtoWithDetails(item);

        LocalDateTime now = LocalDateTime.now();

        bookings.stream()
                .filter(b -> b.getItem().getId().equals(item.getId()))
                .forEach(b -> {
                    if (b.getStart().isAfter(now)) {
                        dto.setNextBooking(
                                Stream.concat(Stream.ofNullable(dto.getNextBooking()), Stream.of(b))
                                        .min(Comparator.comparing(BookingDtoForItem::getStart))
                                        .orElse(null)
                        );
                    } else {
                        dto.setLastBooking(
                                Stream.concat(Stream.ofNullable(dto.getLastBooking()), Stream.of(b))
                                        .max(Comparator.comparing(BookingDtoForItem::getEnd))
                                        .orElse(null)
                        );
                    }
                });
        return dto;
    }
}
