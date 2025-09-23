package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        UserDto requestDto = new UserDto(1L, "Иван", "ivan@example.com");
        Mockito.when(userService.create(any(UserDto.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserDto requestDto = new UserDto(null, "Петр", "petr@example.com");
        UserDto responseDto = new UserDto(1L, "Петр", "petr@example.com");

        Mockito.when(userService.update(eq(1L), any(UserDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Петр"))
                .andExpect(jsonPath("$.email").value("petr@example.com"));
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        UserDto responseDto = new UserDto(2L, "Ольга", "olga@example.com");
        Mockito.when(userService.getById(2L)).thenReturn(responseDto);

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Ольга"))
                .andExpect(jsonPath("$.email").value("olga@example.com"));
    }

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "Иван", "ivan@example.com"),
                new UserDto(2L, "Ольга", "olga@example.com")
        );

        Mockito.when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].email").value("ivan@example.com"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/3"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(3L);
    }
}
