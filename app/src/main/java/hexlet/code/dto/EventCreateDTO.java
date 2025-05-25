package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class EventCreateDTO {
    @NotBlank
    @Size(min = 1)
    private String title;

    private String description;

    private String eventDate;

    @JsonProperty("category_ids")
    private Set<Long> categoryIds = new HashSet<>();

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    private String status;
}

