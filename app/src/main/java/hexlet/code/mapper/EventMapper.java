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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Mapping(target = "categoryIds", expression = "java(mapCategoriesToIds(event.getCategories()))")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "createdAt", target = "createdAt")
    public abstract EventDTO map(Event event);

    protected Set<Long> mapCategoriesToIds(Set<Category> categories) {
        if (categories == null) {
            return Collections.emptySet();
        }
        return categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    @Mapping(source = "status", target = "eventStatus", qualifiedByName = "mapStatus")
    @Mapping(target = "categories", expression = "java(mapCategories(dto.getCategoryIds()))")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "mapAssigneeId")
    @Mapping(source = "eventDate", target = "eventDate")
    public abstract Event map(EventCreateDTO dto);

    protected Set<Category> mapCategories(Set<Long> categoryIds) {
        if (categoryIds == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(categoryRepository.findAllById(categoryIds));
    }

    @Named("mapStatus")
    protected EventStatus mapStatus(String slug) {
        return eventStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("EventStatus not found"));
    }


    @Named("mapAssigneeId")
    protected User mapAssigneeId(Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Mapping(source = "status", target = "eventStatus", qualifiedByName = "mapStatus")
    @Mapping(source = "assigneeId", target = "assignee", qualifiedByName = "mapAssigneeId")
    @Mapping(target = "categories", expression = "java(mapCategories(eventUpdateDTO.getCategoryIds().orElse(null)))")
    public abstract void update(EventUpdateDTO eventUpdateDTO, @MappingTarget Event event);
}


