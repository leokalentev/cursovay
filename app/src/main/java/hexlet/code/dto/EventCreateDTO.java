package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventCreateDTO {
    @NotBlank
    @Size(min = 1)
    private String title;

    private String description;

    private String eventDate;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    private String status;
}

