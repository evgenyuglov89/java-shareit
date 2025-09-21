package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({
        ItemRequestServiceImpl.class,
        ItemRequestMapper.class,
        ItemMapper.class
})
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void addItemRequest_shouldCreateRequest() {
        User user = userRepository.save(new User(null, "Петр", "petr@example.com"));
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужна дрель");

        ItemRequestDto result = itemRequestService.addItemRequest(user.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");

        assertThat(itemRequestRepository.findById(result.getId())).isPresent();
    }

    @Test
    void addItemRequest_shouldThrowWhenUserNotFound() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Нужен молоток");

        assertThatThrownBy(() -> itemRequestService.addItemRequest(999L, dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void getUserItemRequests_shouldReturnRequestsWithItems() {
        User user = userRepository.save(new User(null, "Иван", "ivan@example.com"));

        ItemRequest req1 = itemRequestRepository.save(
                new ItemRequest(null, "Хочу велосипед", LocalDateTime.now(), user)
        );
        ItemRequest req2 = itemRequestRepository.save(
                new ItemRequest(null, "Хочу ноутбук", LocalDateTime.now(), user)
        );

        itemRepository.save(new Item(null, "Велосипед", "Горный", true, user.getId(), req1.getId()));
        itemRepository.save(new Item(null, "Ноутбук", "Игровой", true, user.getId(), req2.getId()));

        Collection<ItemRequestExtendedDto> result = itemRequestService.getUserItemRequests(user.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .anySatisfy(r -> {
                    if (r.getDescription().equals("Хочу велосипед")) {
                        assertThat(r.getItems()).hasSize(1);
                        assertThat(r.getItems().get(0).getName()).isEqualTo("Велосипед");
                    }
                });
    }

    @Test
    void getUserItemRequests_shouldThrowWhenUserNotFound() {
        assertThatThrownBy(() -> itemRequestService.getUserItemRequests(123L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getItemRequests_shouldReturnAll() {
        User user = userRepository.save(new User(null, "Анна", "anna@example.com"));
        itemRequestRepository.save(new ItemRequest(null, "Книга", LocalDateTime.now(), user));

        Collection<ItemRequestDto> result = itemRequestService.getItemRequests();

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getDescription()).isEqualTo("Книга");
    }

    @Test
    void getItemRequest_shouldReturnSingleRequest() {
        User user = userRepository.save(new User(null, "Максим", "max@example.com"));
        ItemRequest req = itemRequestRepository.save(new ItemRequest(null, "Фотоаппарат", LocalDateTime.now(), user));

        itemRepository.save(new Item(null, "Canon", "DSLR", true, user.getId(), req.getId()));

        ItemRequestExtendedDto result = itemRequestService.getItemRequest(req.getId());

        assertThat(result.getDescription()).isEqualTo("Фотоаппарат");
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void getItemRequest_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> itemRequestService.getItemRequest(999L))
                .isInstanceOf(ItemRequestNotFoundException.class)
                .hasMessageContaining("Не найден запрос");
    }
}