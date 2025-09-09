package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

@Builder
@Data
public class ItemDetailsDto {

    private Long id;

    @NotBlank(message = "name не может быть пустым")
    private String name;

    @NotBlank(message = "description не может быть пустым")
    private String description;

    @BooleanFlag
    @NotNull(message = "available не может быть пустым")
    private Boolean available;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDto> comments;
}
