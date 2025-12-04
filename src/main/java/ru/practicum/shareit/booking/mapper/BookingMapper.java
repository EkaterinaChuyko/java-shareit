package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class BookingMapper {

    public Booking toEntity(BookingRequestDto dto, Item item, User booker) {
        return Booking.builder().item(item).booker(booker).start(dto.getStart()).end(dto.getEnd()).build();
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) return null;

        BookingResponseDto.Item itemDto = new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName(), booking.getItem().getDescription(), booking.getItem().getAvailable(), booking.getItem().getOwnerId(), booking.getItem().getRequestId());

        BookingResponseDto.Booker bookerDto = new BookingResponseDto.Booker(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail());

        return new BookingResponseDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus(), bookerDto, itemDto);
    }
}