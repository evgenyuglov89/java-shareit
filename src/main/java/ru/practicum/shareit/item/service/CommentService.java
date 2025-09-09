package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        UserDto userDto = userService.getById(userId);

        List<Booking> pastBookings = bookingRepository.findPastByBookerId(
                userId, Sort.by(Sort.Direction.DESC, "end"));

        if (pastBookings.isEmpty()) {
            throw new BadRequestException("Товар с id:" + itemId + " не был забронирован");
        }

        if (pastBookings.stream()
                .map(Booking::getItem)
                .noneMatch(item -> item.getId().equals(itemId))) {
            throw new BadRequestException("Заказчик с id:" + userId +
                    " не забрал товар с id:" + itemId +
                    " или срок бронирования еще не истек");
        }

        Item item = pastBookings.stream()
                .map(Booking::getItem)
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Невозможно добавить комментарий — бронирование не найдено")
                );

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(userMapper.toUser(userDto));

        commentRepository.save(comment);

        CommentDto result = commentMapper.toCommentDto(comment);
        result.setAuthorName(userDto.getName());

        return result;
    }
}
