package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void search_shouldReturnMatchingItems() {
        itemRepository.save(Item.builder().name("Drill").description("Power drill").available(true).ownerId(1L).build());

        itemRepository.save(Item.builder().name("Hammer").description("Steel hammer").available(true).ownerId(1L).build());

        List<Item> found = itemRepository.searchAvailableItems("drill");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Drill");
    }
}