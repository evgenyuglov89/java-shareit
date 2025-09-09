package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoForItem {
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Item item;

    private Long bookerId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Long id;

        private String name;
    }
}
