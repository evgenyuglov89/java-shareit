package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {
    @Positive
    private Long id;
    @NotBlank
    private String text;
    @NotBlank
    private String authorName;
    private LocalDateTime created;
}
