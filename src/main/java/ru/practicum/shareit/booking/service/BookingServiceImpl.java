package ru.practicum.shareit.booking.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Validated
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public @Valid BookingDto create(long userId, BookingCreateDto dto) {
        Item item = getAvailableItemOrThrow(dto.getItemId());
        validateNotOwner(userId, item);

        User user = getUserOrThrow(userId);

        Booking booking = bookingMapper.mapToEntity(dto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        return bookingMapper.mapToDto(booking);
    }

    @Override
    @Transactional
    public @Valid BookingDto updateStatus(long bookingId, boolean approved, long userId) {
        Booking booking = getBookingOrThrow(bookingId);
        validateOwner(userId, booking);

        if (booking.getStatus() == BookingStatus.APPROVED && approved) {
            throw new BadRequestException("Бронь уже одобрена");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return bookingMapper.mapToDto(booking);
    }

    @Override
    public @Valid BookingDto getById(long bookingId, long userId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (booking.getBooker().getId() != userId && booking.getItem().getOwnerId() != userId) {
            throw new BookingNotFoundException("Бронь может быть просмотрена только владельцем или заказчиком");
        }

        return bookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(BookingState state, long userId) {
        validateUserExists(userId);
        return findBookings(state, userId, true);
    }

    @Override
    public List<BookingDto> getAllByOwner(BookingState state, long userId) {
        validateUserExists(userId);
        return findBookings(state, userId, false);
    }

    private Item getAvailableItemOrThrow(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Товар с id: " + itemId + " не найден"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Товар с id: " + itemId + " недосупен");
        }
        return item;
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    private Booking getBookingOrThrow(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь с id: " + bookingId + " не найдена"));
    }

    private void validateNotOwner(long userId, Item item) {
        if (Objects.equals(item.getOwnerId(), userId)) {
            throw new BadRequestException("Владалец не может забронировать свой собственный товар");
        }
    }

    private void validateOwner(long userId, Booking booking) {
        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new BadRequestException("Товар не пренадлежит владельцу с id: " + userId);
        }
    }

    private void validateUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }

    private List<BookingDto> findBookings(BookingState state, long userId, boolean byBooker) {
        Sort sort = Sort.by(Sort.Direction.DESC, "end");

        List<Booking> bookings = switch (state) {
            case ALL -> byBooker
                    ? bookingRepository.findAllByBookerId(userId, sort)
                    : bookingRepository.findAllByItemOwnerId(userId, sort);
            case CURRENT -> byBooker
                    ? bookingRepository.findCurrentByBookerId(userId, sort)
                    : bookingRepository.findCurrentByOwnerId(userId, sort);
            case PAST -> byBooker
                    ? bookingRepository.findPastByBookerId(userId, sort)
                    : bookingRepository.findPastByOwnerId(userId, sort);
            case FUTURE -> byBooker
                    ? bookingRepository.findFutureByBookerId(userId, sort)
                    : bookingRepository.findFutureByOwnerId(userId, sort);
            case WAITING -> byBooker
                    ? bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sort)
                    : bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> byBooker
                    ? bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort)
                    : bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
        };

        return bookingMapper.mapToDtoList(bookings);
    }
}
