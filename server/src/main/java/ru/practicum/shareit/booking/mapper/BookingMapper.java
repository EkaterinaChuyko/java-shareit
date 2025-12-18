package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.user.User;


@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDto bookingRequestDto, Item item, User booker) {
        if (bookingRequestDto == null) return null;

        return Booking.builder().start(bookingRequestDto.getStart()).end(bookingRequestDto.getEnd()).item(item).booker(booker).status(ru.practicum.shareit.booking.model.BookingStatus.WAITING).build();
    }

    public BookingResponseDto toDto(Booking booking) {
        if (booking == null) return null;

        BookingResponseDto.Item itemDto = new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName(), booking.getItem().getDescription(), booking.getItem().getAvailable(), booking.getItem().getOwnerId(), booking.getItem().getRequestId());

        BookingResponseDto.Booker bookerDto = new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail());

        return new BookingResponseDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus(), bookerDto, itemDto);
    }
}