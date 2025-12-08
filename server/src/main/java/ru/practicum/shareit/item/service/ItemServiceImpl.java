package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Item availability must be specified");
        }

        Item item = itemMapper.toEntity(itemDto, ownerId);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingsDto getByIdWithBookings(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        ItemWithBookingsDto dto = itemMapper.toItemWithBookingsDto(item);

        if (item.getOwnerId().equals(userId)) {
            addBookingInfo(dto, item.getId());
        }
        addCommentsInfo(dto, item.getId());

        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getAllByOwnerIdWithBookings(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Comment>> commentsByItemId = commentRepository.findByItemIdInOrderByCreatedDesc(itemIds).stream().collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream().map(item -> {
            ItemWithBookingsDto dto = itemMapper.toItemWithBookingsDto(item);
            addBookingInfo(dto, item.getId());

            List<Comment> comments = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());
            dto.setComments(comments.stream().map(commentMapper::toDto).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemAccessDeniedException("Only owner can update item");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
            item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepository.searchAvailableItems(text).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        User author = userRepository.findById(authorId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found"));

        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasBooked)
            throw new CommentValidationException("You can only comment on items you have booked in the past");

        Comment comment = Comment.builder().text(commentDto.getText()).author(author).item(item).created(LocalDateTime.now()).build();

        return commentMapper.toDto(commentRepository.save(comment));
    }

    private void addBookingInfo(ItemWithBookingsDto dto, Long itemId) {
        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findLastBooking(itemId, now).ifPresent(b -> dto.setLastBooking(new ItemWithBookingsDto.BookingInfo(b.getId(), b.getBooker().getId(), b.getStart(), b.getEnd())));

        bookingRepository.findNextBooking(itemId, now).ifPresent(b -> dto.setNextBooking(new ItemWithBookingsDto.BookingInfo(b.getId(), b.getBooker().getId(), b.getStart(), b.getEnd())));
    }

    private void addCommentsInfo(ItemWithBookingsDto dto, Long itemId) {
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        dto.setComments(comments.stream().map(commentMapper::toDto).collect(Collectors.toList()));
    }
}