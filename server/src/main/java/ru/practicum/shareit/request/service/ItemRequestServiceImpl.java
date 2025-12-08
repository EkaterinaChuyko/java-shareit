package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.user.User;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequest create(String description, Long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(description);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return savedRequest;
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return requests;
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);
        return requests;
    }

    @Override
    public ItemRequest getRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        return request;
    }
}