package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class EventStatusUpdateDTO {
    private JsonNullable<String> name = JsonNullable.undefined();
    private JsonNullable<String> slug = JsonNullable.undefined();
}

