package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventDTO {
    private Long id;

    private String title;

    private String description;

    private String status;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String createdAt;

    private String eventDate;
    private Boolean canEdit;
}
