package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class ItemServiceImplTest {

    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final ItemMapper itemMapper = Mockito.mock(ItemMapper.class);
    private final CommentMapper commentMapper = Mockito.mock(CommentMapper.class);

    private final ItemServiceImpl service = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemMapper, commentMapper);

    @Test
    void create_whenUserNotFound_shouldThrow() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ItemDto dto = new ItemDto(null, "item", "desc", true, null, null);

        assertThrows(UserNotFoundException.class, () -> service.create(dto, 1L));
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        User user = new User(1L, "u", "u@mail.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Item item = new Item(1L, "item", "desc", true, 1L, null);

        Mockito.when(itemMapper.toEntity(any(ItemDto.class), anyLong())).thenReturn(item);

        Mockito.when(itemRepository.save(any())).thenReturn(item);

        Mockito.when(itemMapper.toDto(any())).thenReturn(new ItemDto(1L, "item", "desc", true, 1L, null));
        ItemDto result = service.create(new ItemDto(null, "item", "desc", true, null, null), 1L);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("item");
    }
}