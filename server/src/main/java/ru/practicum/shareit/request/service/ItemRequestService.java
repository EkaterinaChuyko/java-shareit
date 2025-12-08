package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(String description, Long userId);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequest getRequestById(Long requestId, Long userId);
}