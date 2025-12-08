import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ru.practicum.shareit.ShareItGateway.class)
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingRequestDto dto = new BookingRequestDto(1L, start, end);

        JsonContent<BookingRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").startsWith(start.toString().substring(0, 16));
        assertThat(result).extractingJsonPathStringValue("$.end").startsWith(end.toString().substring(0, 16));
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonString = """
                {
                  "itemId": 5,
                  "start": "2030-01-01T10:00:00",
                  "end": "2030-01-02T10:00:00"
                }
                """;

        BookingRequestDto dto = json.parse(jsonString).getObject();

        assertThat(dto.getItemId()).isEqualTo(5);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.parse("2030-01-01T10:00:00"));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.parse("2030-01-02T10:00:00"));
    }
}