package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class EventUpdateDTO {
    private JsonNullable<String> title = JsonNullable.undefined();

    private JsonNullable<String> description = JsonNullable.undefined();

    private JsonNullable<String> eventDate = JsonNullable.undefined();

    private JsonNullable<String> status = JsonNullable.undefined();

    @JsonProperty("category_ids")
    private JsonNullable<Set<Long>> categoryIds = JsonNullable.undefined();

    private JsonNullable<Long> assigneeId = JsonNullable.undefined();
}

