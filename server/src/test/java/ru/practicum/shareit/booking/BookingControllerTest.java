package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_shouldReturnBookingDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                100L,
                "name",
                "description",
                false,
                1L,
                1L);
        UserDto userDto = new UserDto(200L, "name", "test@test.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2025, 9, 12, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 9, 13, 10, 0));
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(BookingStatus.APPROVED);

        BookingCreateDto createDto = new BookingCreateDto(bookingDto.getStart(), bookingDto.getEnd(), 100L);

        Mockito.when(bookingService.create(eq(1L), any(BookingCreateDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.item.id").value(100L))
                .andExpect(jsonPath("$.booker.name").value("name"));
    }

    @Test
    void updateStatus_shouldReturnUpdatedBooking() throws Exception {
        BookingDto updatedDto = new BookingDto();
        updatedDto.setId(1L);
        updatedDto.setStart(LocalDateTime.now());
        updatedDto.setEnd(LocalDateTime.now().plusDays(1));
        updatedDto.setStatus(BookingStatus.REJECTED);

        Mockito.when(bookingService.updateStatus(eq(1L), eq(false), eq(1L)))
                .thenReturn(updatedDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingDto.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingService.getById(eq(1L), eq(1L)))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllByBooker_shouldReturnList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setStatus(BookingStatus.APPROVED);

        Mockito.when(bookingService.getAllByBooker(eq(BookingState.ALL), eq(1L)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void getAllByOwner_shouldReturnList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(2L);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(BookingStatus.WAITING);

        Mockito.when(bookingService.getAllByOwner(eq(BookingState.ALL), eq(1L)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}