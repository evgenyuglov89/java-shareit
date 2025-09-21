package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingAggregatorService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentAggregatorService;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({
        ItemServiceImpl.class,
        ItemMapper.class,
        UserServiceImpl.class,
        BookingAggregatorService.class,
        CommentAggregatorService.class,
        CommentService.class,
        UserMapper.class,
        BookingMapper.class,
        CommentMapper.class
})
class ItemServiceTest {

    @Autowired private ItemServiceImpl itemService;
    @Autowired private UserRepository userRepository;
    @Autowired private ItemRepository itemRepository;

    @Test
    void create_shouldSaveItem() {
        User user = userRepository.save(new User(null, "Петр", "petr@example.com"));

        ItemDto dto = new ItemDto(null, "name", "description", true, user.getId(), null);

        ItemDto result = itemService.create(dto, user.getId());

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("name");
        assertThat(itemRepository.findById(result.getId())).isPresent();
    }

    @Test
    void update_shouldModifyItem() {
        User user = userRepository.save(new User(null, "Анна", "anna@example.com"));
        Item item = itemRepository.save(new Item(null, "Старая", "Описание", true, user.getId(), null));

        ItemDto updateDto = new ItemDto(null, "name2", "description3", true, 4L, 5L);

        ItemDto result = itemService.update(item.getId(), updateDto, user.getId());

        assertThat(result.getName()).isEqualTo("name2");
        assertThat(itemRepository.findById(item.getId())).get()
                .extracting(Item::getName).isEqualTo("name2");
    }

    @Test
    void update_shouldThrowIfNotOwner() {
        User owner = userRepository.save(new User(null, "Анна", "anna@example.com"));
        User stranger = userRepository.save(new User(null, "Иван", "ivan@example.com"));
        Item item = itemRepository.save(new Item(null, "Телефон", "Описание", true, owner.getId(), null));

        ItemDto updateDto = new ItemDto(null, "name3", "description3", true, 6L, 7L);

        assertThatThrownBy(() -> itemService.update(item.getId(), updateDto, stranger.getId()))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void getAllByOwner_shouldReturnItems() {
        User user = userRepository.save(new User(null, "Олег", "oleg@example.com"));
        itemRepository.save(new Item(null, "Книга", "Толстая", true, user.getId(), null));

        List<ItemDetailsDto> result = itemService.getAllByOwner(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Книга");
    }

    @Test
    void search_shouldReturnMatchingItems() {
        User user = userRepository.save(new User(null, "Павел", "pavel@example.com"));
        itemRepository.save(new Item(null, "Дрель", "Мощная", true, user.getId(), null));
        itemRepository.save(new Item(null, "Молоток", "Крепкий", true, user.getId(), null));

        List<ItemDto> result = itemService.search("дрель");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Дрель");
    }
}
