package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addItemRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("Нужен ноутбук");
        ItemRequestDto responseDto = new ItemRequestDto(1L, "Нужен ноутбук", LocalDateTime.now(), 3L);

        Mockito.when(itemRequestService.addItemRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужен ноутбук"));
    }

    @Test
    void getUserItemRequests_shouldReturnList() throws Exception {
        List<ItemRequestExtendedDto> responseList = List.of(
                new ItemRequestExtendedDto(1L, "Хочу гитару", LocalDateTime.now(), 3L, List.of())
        );

        Mockito.when(itemRequestService.getUserItemRequests(eq(1L)))
                .thenReturn(responseList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Хочу гитару"));
    }

    @Test
    void getItemRequests_shouldReturnList() throws Exception {
        List<ItemRequestDto> responseList = List.of(
                new ItemRequestDto(2L, "Хочу принтер", LocalDateTime.now(), 5L)
        );

        Mockito.when(itemRequestService.getItemRequests()).thenReturn(responseList);

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].description").value("Хочу принтер"));
    }

    @Test
    void getItemRequest_shouldReturnSingleRequest() throws Exception {
        ItemRequestExtendedDto responseDto =
                new ItemRequestExtendedDto(3L, "Хочу велосипед", LocalDateTime.now(), 7L, List.of());

        Mockito.when(itemRequestService.getItemRequest(eq(3L))).thenReturn(responseDto);

        mockMvc.perform(get("/requests/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.description").value("Хочу велосипед"));
    }
}
