package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId());
    }

    public Item toEntity(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) {
            return null;
        }
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), ownerId, itemDto.getRequestId());
    }
}