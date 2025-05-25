package hexlet.code.specification;

import hexlet.code.dto.EventDTO;
import hexlet.code.model.Category;
import hexlet.code.model.Event;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Set;

@Component
public class EventSpecification {

    public Specification<Event> build(EventDTO params) {
        return Specification.where(titleContains(params.getTitle()))
                .and(statusIs(params.getStatus()))
                .and(hasAnyCategory(params.getCategoryIds()))
                .and(eventDateIs(params.getEventDate()));
    }

    private Specification<Event> titleContains(String substr) {
        if (substr == null || substr.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + substr.toLowerCase() + "%");
    }

    private Specification<Event> statusIs(String slug) {
        if (slug == null || slug.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) ->
                cb.equal(root.get("eventStatus").get("slug"), slug);
    }

    private Specification<Event> hasAnyCategory(Set<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            Join<Event, Category> categories = root.join("categories", JoinType.LEFT);
            return categories.get("id").in(categoryIds);
        };
    }

    private Specification<Event> eventDateIs(String date) {
        if (date == null || date.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
            LocalDate eventDate = LocalDate.parse(date);
            return cb.equal(root.get("eventDate"), eventDate);
        };
    }
}

