package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentAggregatorService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public List<ItemDetailsDto> addComments(List<ItemDetailsDto> items) {
        if (items.isEmpty()) {
            return items;
        }

        List<Long> itemIds = items.stream()
                .map(ItemDetailsDto::getId)
                .toList();

        List<Comment> comments = commentRepository.findAllByItemsId(itemIds);

        Map<Long, List<CommentDto>> commentsByItemId = comments.stream()
                .map(comment -> {
                    CommentDto dto = commentMapper.toCommentDto(comment);
                    dto.setAuthorName(comment.getAuthor().getName());
                    return Map.entry(comment.getItem().getId(), dto);
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        items.forEach(item -> {
            List<CommentDto> itemComments = commentsByItemId.getOrDefault(item.getId(), List.of());
            item.setComments(itemComments);
        });

        return items;
    }

    public ItemDetailsDto addComments(ItemDetailsDto item) {
        return addComments(List.of(item)).get(0);
    }
}
