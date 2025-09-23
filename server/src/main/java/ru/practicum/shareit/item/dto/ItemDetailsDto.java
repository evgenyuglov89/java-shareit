package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetailsDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDto> comments;
}
