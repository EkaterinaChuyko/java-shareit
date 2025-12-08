package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.util.HeaderConstants;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    private static final String BOOKING_ID_PATH = "/{bookingId}";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingClient.create(bookingRequestDto, userId);
    }

    @PatchMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> updateStatus(@PathVariable Long bookingId, @RequestParam Boolean approved, @RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId) {
        return bookingClient.updateStatus(bookingId, approved, userId);
    }

    @GetMapping(BOOKING_ID_PATH)
    public ResponseEntity<Object> getById(@PathVariable Long bookingId, @RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId) {
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getOwnerBookings(userId, state);
    }
}