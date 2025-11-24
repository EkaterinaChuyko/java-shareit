package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.info("Creating item '{}' for owner {}", itemDto.getName(), ownerId);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemMapper.toEntity(itemDto, owner.getId());
        Item savedItem = itemRepository.save(item);
        log.info("Item created with id {}", savedItem.getId());
        return itemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto getById(Long id) {
        log.info("Fetching item with id {}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(Long ownerId) {
        log.info("Fetching all items for owner {}", ownerId);
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        log.info("Updating item {} by owner {}", id, ownerId);
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!ownerId.equals(existingItem.getOwnerId())) {
            log.warn("Owner {} tried to update item {} which they do not own", ownerId, id);
            throw new ItemAccessDeniedException("Only owner can update item");
        }

        if (itemDto.getName() != null) existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existingItem.setAvailable(itemDto.getAvailable());

        Item updatedItem = itemRepository.update(existingItem);
        log.info("Item {} updated successfully", updatedItem.getId());
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Searching items with text '{}'", text);
        return itemRepository.search(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}