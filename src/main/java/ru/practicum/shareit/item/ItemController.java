package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import jakarta.validation.Valid;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.HeaderConstants.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(itemDto, ownerId));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getById(@PathVariable Long itemId, @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getByIdWithBookings(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getAllByOwnerIdWithBookings(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.update(itemId, itemDto, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return commentService.addComment(userId, itemId, commentDto);
    }
}