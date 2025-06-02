package hexlet.code.mapper;

import hexlet.code.dto.EventCreateDTO;
import hexlet.code.dto.EventDTO;
import hexlet.code.dto.EventUpdateDTO;
import hexlet.code.model.Category;
import hexlet.code.model.Event;
import hexlet.code.model.EventStatus;
import hexlet.code.model.User;
import hexlet.code.repository.CategoryRepository;
import hexlet.code.repository.EventStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EventMapper {

    @Autowired
    private EventStatusRepository eventStatusRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "eventStatus.slug", target = "status")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "createdAt", target = "createdAt")
    public abstract EventDTO map(Event event);

    @Mapping(source = "status", target = "eventStatus", qualifiedByName = "mapStatus")
    @Mapping(source = "categoryId", target = "category", qualifiedByName = "mapCategoryId")
    @Mapping(source = "title",   target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    public abstract Event map(EventCreateDTO dto);

    @Named("mapStatus")
    protected EventStatus mapStatus(String slug) {
        return eventStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("EventStatus не найден"));
    }

    @Named("mapCategoryId")
    protected Category mapCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category не найдена"));
    }

    @Named("mapAssigneeId")
    protected User mapAssigneeId(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("User не найден"));
    }

    @Mapping(source = "status", target = "eventStatus", qualifiedByName = "mapStatus")
    @Mapping(source = "categoryId", target = "category", qualifiedByName = "mapCategoryId")
    @Mapping(source = "assigneeId", target = "assignee",    qualifiedByName = "mapAssigneeId")
    public abstract void update(EventUpdateDTO dto, @MappingTarget Event event);
}


