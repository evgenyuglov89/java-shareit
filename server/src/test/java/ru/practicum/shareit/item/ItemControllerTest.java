package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void create_shouldReturnItem() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Ноутбук", "Игровой", true, null, null);
        ItemDto responseDto = new ItemDto(1L, "Ноутбук", "Игровой", true, null, null);

        Mockito.when(itemService.create(any(ItemDto.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ноутбук"));
    }

    @Test
    void update_shouldReturnUpdatedItem() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Мышка", "Беспроводная", true, null, null);
        ItemDto responseDto = new ItemDto(1L, "Мышка", "Беспроводная", true, null, null);

        Mockito.when(itemService.update(eq(1L), any(ItemDto.class), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Мышка"));
    }

    @Test
    void getById_shouldReturnItemDetails() throws Exception {
        ItemDetailsDto responseDto = new ItemDetailsDto(
                1L,
                "Стол",
                "Офисный",
                true,
                null,
                null,
                List.of()
        );

        Mockito.when(itemService.getById(eq(1L), eq(1L))).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Стол"));
    }

    @Test
    void getAllByOwner_shouldReturnListOfItems() throws Exception {
        List<ItemDetailsDto> responseList = List.of(
                new ItemDetailsDto(1L, "Стол", "Офисный", true, null, null, List.of())
        );

        Mockito.when(itemService.getAllByOwner(eq(1L))).thenReturn(responseList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Стол"));
    }

    @Test
    void search_shouldReturnListOfItems() throws Exception {
        List<ItemDto> responseList = List.of(
                new ItemDto(1L, "Стул", "Деревянный", true, null, null)
        );

        Mockito.when(itemService.search(eq("стул"))).thenReturn(responseList);

        mockMvc.perform(get("/items/search")
                        .param("text", "стул"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Стул"));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentCreateDto requestDto = new CommentCreateDto("Отличная вещь");
        CommentDto responseDto = new CommentDto(1L, "Отличная вещь", "Автор", null);

        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(CommentCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Отличная вещь"));
    }
}
