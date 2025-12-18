package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    private final ItemMapper itemMapper;

    public ItemRequestMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public ItemRequestDto toDto(ItemRequest request, List<Item> items) {
        if (request == null) {
            return null;
        }

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        if (items != null && !items.isEmpty()) {
            List<ItemDto> itemDtos = items.stream().map(itemMapper::toDto).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }

    public ItemRequest toEntity(ItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ItemRequest request = new ItemRequest();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setCreated(dto.getCreated());
        return request;
    }
}