package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class EventDTO {
    private Long id;

    private String title;

    private String description;

    private String status;

    @JsonProperty("category_ids")
    private Set<Long> categoryIds;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String createdAt;

    private String eventDate;
}
