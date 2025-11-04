package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> search(String text);

    Optional<Item> findById(Long id);

    List<Item> findAllByOwnerId(Long ownerId);

    Item save(Item item);

    Item update(Item item);

    void deleteById(Long id);
}
