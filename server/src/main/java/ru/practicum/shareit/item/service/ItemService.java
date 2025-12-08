package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemWithBookingsDto getByIdWithBookings(Long id, Long userId);

    List<ItemWithBookingsDto> getAllByOwnerIdWithBookings(Long ownerId);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId);
}