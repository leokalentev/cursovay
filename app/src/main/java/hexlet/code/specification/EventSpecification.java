package hexlet.code.specification;

import hexlet.code.dto.EventDTO;
import hexlet.code.model.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EventSpecification {

    public Specification<Event> build(EventDTO params) {
        return Specification.where(titleContains(params.getTitle()))
                .and(statusIs(params.getStatus()))
                .and(hasCategory(params.getCategoryId()))
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

    private Specification<Event> hasCategory(Long categoryId) {
        if (categoryId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
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
