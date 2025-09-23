package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({UserServiceImpl.class, UserMapper.class})
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void create_shouldPersistUserAndReturnDto() {
        UserDto dto = new UserDto(null, "Иван", "ivan@example.com");

        UserDto saved = userService.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Иван");
        assertThat(userRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void update_shouldChangeFields() {
        User user = new User(null, "Старое имя", "old@example.com");
        user = userRepository.save(user);

        UserDto updateDto = new UserDto(null, "Новое имя", "new@example.com");

        UserDto updated = userService.update(user.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("Новое имя");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        UserDto updateDto = new UserDto(null, "Имя", "email@example.com");

        assertThatThrownBy(() -> userService.update(999L, updateDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getById_shouldReturnDto() {
        User user = userRepository.save(new User(null, "Имя", "email@example.com"));

        UserDto dto = userService.getById(user.getId());

        assertThat(dto.getName()).isEqualTo("Имя");
        assertThat(dto.getEmail()).isEqualTo("email@example.com");
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> userService.getById(123L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        userRepository.save(new User(null, "A", "a@example.com"));
        userRepository.save(new User(null, "B", "b@example.com"));

        List<UserDto> all = userService.getAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void delete_shouldRemoveUser() {
        User user = userRepository.save(new User(null, "Test", "test@example.com"));

        userService.delete(user.getId());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void delete_shouldNotFailForNonExistingId() {
        assertThatCode(() -> userService.delete(123L))
                .doesNotThrowAnyException();
    }
}
