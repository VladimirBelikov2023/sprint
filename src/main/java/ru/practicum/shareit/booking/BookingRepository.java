package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> getByItemIdAndStartingIsBeforeAndStatus(int id, LocalDateTime time, Status status, Sort sort);


    List<Booking> getByItemIdAndStartingIsAfterAndStatus(int id, LocalDateTime time, Status status, Sort sort);


    List<Booking> findByEndingIsBeforeAndBookerIdOrderByEndingDesc(LocalDateTime end, int id);

    @Query("select new ru.practicum.shareit.booking.Booking(b.id, b.starting, b.ending, b.item, b.booker, b.status) from Booking as b  where b.item.id in (select i.id from Item i where i.owner.id = ?1) and b.ending < now() order by ending desc ")
    List<Booking> findAllLastBookingOwner(int id);

    List<Booking> findByStartingIsAfterAndBookerIdOrderByEndingDesc(LocalDateTime end, int id);


    @Query("select b from Booking as b  where b.item.id in (select i.id from Item i where i.owner.id = ?1)  and b.starting > now() order by ending desc ")
    List<Booking> findAllFutureBookingOwner(int id);


    List<Booking> findByStartingIsBeforeAndEndingIsAfterAndBookerId(LocalDateTime time, LocalDateTime time2, int id, Sort sort);

    List<Booking> findByStatusAndEndingIsBefore(Status status, LocalDateTime time, Sort sort);

    @Query("select b from Booking as b  where b.item.id in (select i.id from Item i where i.owner.id = ?1) and b.starting <= now() and b.ending > now() order by ending desc ")
    List<Booking> findAllCurrentBookingOwner(int id);

    List<Booking> findByStatusAndBookerId(Status status, int id, Sort sort);

    @Query("select b from Booking as b  where b.item.id in (select i.id from Item i where i.owner.id = ?1) and b.status like 'WAITING' order by ending desc ")
    List<Booking> findAllWaitingBookingOwner(int id);


    List<Booking> findByBookerIdAndStatus(int id, Status status, Sort sort);

    @Query("select b from Booking as b  where b.item.id in (select i.id from Item i where i.owner.id = ?1) and b.status like 'REJECTED' order by ending desc ")
    List<Booking> findAllRejectedBookingOwner(int id);

    List<Booking> findByBookerId(int id, Sort sort);

    @Query("select b from Booking as b where b.item.id in (select i.id from Item i where i.owner.id = ?1)  order by ending desc ")
    List<Booking> findAllOrderedBookingOwner(int id);
}
