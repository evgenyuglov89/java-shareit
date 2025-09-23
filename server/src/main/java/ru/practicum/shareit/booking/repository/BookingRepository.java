package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastByBookerId(@Param("bookerId") Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureByBookerId(@Param("bookerId") Long bookerId, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIdIn(List<Long> itemsId);
}
