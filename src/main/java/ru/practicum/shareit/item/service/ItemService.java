package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto getById(Long id);

    List<ItemDto> getAllByOwnerId(Long ownerId);

    ItemDto update(Long id, ItemDto itemDto, Long ownerId);

    List<ItemDto> search(String text);
}
