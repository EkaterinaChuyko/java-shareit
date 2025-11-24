package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto dto, Long bookerId) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!item.getAvailable()) throw new ItemNotAvailableException("Item is not available");
        if (item.getOwnerId().equals(bookerId)) throw new OwnItemBookingException("Cannot book own item");

        if (dto.getStart() == null || dto.getEnd() == null || !dto.getStart().isBefore(dto.getEnd()) || dto.getStart().isBefore(LocalDateTime.now())) {
            throw new InvalidStatusException("Invalid booking dates");
        }

        Booking booking = bookingMapper.toEntity(dto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        return bookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto updateStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new BookingAccessDeniedException("Only item owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidStatusException("Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new BookingAccessDeniedException("Access denied");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        return filterBookingsByState(bookings, state).stream().map(bookingMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        return filterBookingsByState(bookings, state).stream().map(bookingMapper::toResponseDto).collect(Collectors.toList());
    }

    private List<Booking> filterBookingsByState(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream().filter(b -> switch (state.toUpperCase()) {
            case "CURRENT" -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
            case "PAST" -> b.getEnd().isBefore(now);
            case "FUTURE" -> b.getStart().isAfter(now);
            case "WAITING" -> b.getStatus() == BookingStatus.WAITING;
            case "REJECTED" -> b.getStatus() == BookingStatus.REJECTED;
            case "ALL" -> true;
            default -> false;
        }).collect(Collectors.toList());
    }
}