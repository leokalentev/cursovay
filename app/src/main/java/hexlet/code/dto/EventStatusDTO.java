package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventStatusDTO {
    private Long id;

    private String name;
    private String slug;

    private String createdAt;
}

