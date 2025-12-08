package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.util.HeaderConstants;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.client.CommentClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId) {
        return itemClient.getAllByOwnerId(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId, @PathVariable Long itemId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        return commentClient.addComment(itemId, commentDto, userId);
    }
}