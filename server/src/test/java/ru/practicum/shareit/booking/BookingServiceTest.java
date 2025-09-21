package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({
        BookingServiceImpl.class,
        BookingMapper.class,
        ItemMapper.class,
        UserMapper.class
})
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Петя", "petr@example.com"));
        booker = userRepository.save(new User(null, "Вася", "vasya@example.com"));
        item = itemRepository.save(new Item(null, "Ноутбук", "Игровой", true, owner.getId(), null));
    }

    @Test
    void create_shouldSaveBooking() {
        BookingCreateDto dto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId()
        );

        BookingDto result = bookingService.create(booker.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingRepository.findAll()).hasSize(1);
    }

    @Test
    void create_shouldThrowIfOwnerTriesToBookOwnItem() {
        BookingCreateDto dto = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId()
        );

        assertThatThrownBy(() -> bookingService.create(owner.getId(), dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Владалец не может забронировать свой собственный товар");
    }

    @Test
    void updateStatus_shouldApproveBooking() {
        Booking booking = bookingRepository.save(
                new Booking(null,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2),
                        item,
                        booker,
                        BookingStatus.WAITING)
        );

        BookingDto result = bookingService.updateStatus(booking.getId(), true, owner.getId());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingRepository.findById(booking.getId())).get()
                .extracting(Booking::getStatus)
                .isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void getById_shouldReturnBookingForOwner() {
        Booking booking = bookingRepository.save(
                new Booking(null,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(2),
                        item,
                        booker,
                        BookingStatus.WAITING)
        );

        BookingDto result = bookingService.getById(booking.getId(), owner.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void getAllByBooker_shouldReturnAllBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.WAITING)
        );

        List<BookingDto> result = bookingService.getAllByBooker(BookingState.ALL, booker.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void getAllByOwner_shouldReturnAllBookings() {
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                booker,
                BookingStatus.WAITING)
        );

        List<BookingDto> result = bookingService.getAllByOwner(BookingState.ALL, owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItem().getId()).isEqualTo(item.getId());
    }
}