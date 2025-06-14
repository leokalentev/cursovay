package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;


@Getter
@Setter
public class EventUpdateDTO {
    private JsonNullable<String> title = JsonNullable.undefined();

    private JsonNullable<String> description = JsonNullable.undefined();

    private JsonNullable<String> eventDate = JsonNullable.undefined();

    private JsonNullable<String> status = JsonNullable.undefined();

    @JsonProperty("category_id")
    private JsonNullable<Long> categoryId = JsonNullable.undefined();

    private JsonNullable<Long> assigneeId = JsonNullable.undefined();
}

