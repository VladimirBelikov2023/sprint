package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ErrorStatusException;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepo userRepo;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, int ownerId) {
        Item item;
        User user;
        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        Optional<User> ownerOptional = userRepo.findById(ownerId);
        if (itemOptional.isEmpty() || ownerOptional.isEmpty()) {
            throw new NoObjectException("Неверные входные данные");
        } else {
            item = itemOptional.get();
            user = ownerOptional.get();
        }
        if (item.getOwner().getId() == ownerId) {
            throw new NoObjectException("Вы владелец вещи");
        }
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setBooker(user);
        bookingDto.setItem(item);
        check(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    public BookingDto approveBooking(int ownerId, int id, boolean isApproved) {
        User owner;
        Booking booking;
        Optional<User> ownerOptional = userRepo.findById(ownerId);
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (ownerOptional.isEmpty() || bookingOptional.isEmpty()) {
            throw new NoObjectException("Неверные входные данные");
        } else {
            owner = ownerOptional.get();
            booking = bookingOptional.get();
        }
        if (booking.getItem().getOwner().getId() != owner.getId()) {
            throw new NoObjectException("Вы не владелец");
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            throw new ValidationException("Запрос уже рассмотрен");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }


    public BookingDto getBooking(int ownerId, int id) {
        User owner;
        Booking booking;
        Optional<User> ownerOptional = userRepo.findById(ownerId);
        Optional<Booking> bookingOptional = bookingRepository.findById(id);

        if (ownerOptional.isEmpty() || bookingOptional.isEmpty()) {
            throw new NoObjectException("Неверные входные данные");
        } else {
            owner = ownerOptional.get();
            booking = bookingOptional.get();
        }
        if (booking.getBooker().getId() != owner.getId() && owner.getId() != booking.getItem().getOwner().getId()) {
            throw new NoObjectException("Вы не можете посмотреть запрос");
        }
        return BookingMapper.toBookingDto(booking);

    }


    public List<BookingDto> getBookingLs(int ownerId, String state) {
        Optional<User> owner = userRepo.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NoObjectException("User не найден");
        }
        return getBooks(state, ownerId, false);
    }

    public List<BookingDto> getBookingLsOwner(int ownerId, String state) {
        Optional<User> owner = userRepo.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NoObjectException("User не найден");
        }
        return getBooks(state, ownerId, true);
    }


    private List<BookingDto> getBooks(String stateStr, int ownerId, boolean isTrue) {
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (Exception e) {
            throw new ErrorStatusException();
        }
        List<BookingDto> answer = new ArrayList<>();
        List<Booking> bookings;
        switch (state) {
            case ALL:
                if (isTrue) {
                    bookings = bookingRepository.findAllOrderedBookingOwner(ownerId);
                } else {
                    bookings = bookingRepository.findByBookerId(ownerId, Sort.by("ending").descending());
                }
                break;
            case PAST:
                if (isTrue) {
                    bookings = bookingRepository.findAllLastBookingOwner(ownerId);
                } else {
                    bookings = bookingRepository.findByEndingIsBeforeAndBookerIdOrderByEndingDesc(LocalDateTime.now(), ownerId);
                }
                break;
            case CURRENT:
                if (isTrue) {
                    bookings = bookingRepository.findAllCurrentBookingOwner(ownerId);
                } else {
                    bookings = bookingRepository.findByStartingIsBeforeAndEndingIsAfterAndBookerId(LocalDateTime.now(), LocalDateTime.now(), ownerId, Sort.by("ending").descending());
                }
                break;
            case FUTURE:
                if (isTrue) {
                    bookings = bookingRepository.findAllFutureBookingOwner(ownerId);
                } else {
                    bookings = bookingRepository.findByStartingIsAfterAndBookerIdOrderByEndingDesc(LocalDateTime.now(), ownerId);
                }
                break;
            case WAITING:
                if (isTrue) {
                    bookings = bookingRepository.findAllWaitingBookingOwner(ownerId);
                } else {
                    bookings = bookingRepository.findByStatusAndBookerId(Status.WAITING, ownerId, Sort.by("ending").descending());
                }
                break;
            case REJECTED:
                if (isTrue) {
                    bookings = bookingRepository.findAllRejectedBookingOwner(ownerId);
                } else {
                    bookings = bookingRepository.findByBookerIdAndStatus(ownerId, Status.REJECTED, Sort.by("ending").descending());
                }
                break;
            default:
                throw new ErrorStatusException();
        }
        for (Booking o : bookings) {
            answer.add(BookingMapper.toBookingDto(o));
        }
        return answer;
    }


    private void check(BookingDto bookingDto) {
        if (!bookingDto.getItem().getAvailable()) {
            throw new ValidationException("Item не доступна");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Не верное время бронирования");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("Не верное время бронирования");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now()) || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Не верное время бронирования");
        }
    }
}
