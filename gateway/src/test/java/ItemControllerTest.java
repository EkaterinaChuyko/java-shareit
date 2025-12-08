import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.client.CommentClient;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ru.practicum.shareit.ShareItGateway.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private CommentClient commentClient;

    @Test
    void create_whenValid_shouldReturnOk() throws Exception {
        ItemDto dto = ItemDto.builder().id(null).name("item").description("desc").available(true).requestId(null).build();

        Mockito.when(itemClient.create(any(), anyLong())).thenReturn(ResponseEntity.ok(dto));

        mvc.perform(post("/items").header("X-Sharer-User-Id", 1).content(mapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}