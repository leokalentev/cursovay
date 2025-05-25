package hexlet.code.repository;

import hexlet.code.model.Event;
import hexlet.code.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    boolean existsByAssigneeId(Long userId);
    boolean existsByEventStatusId(Long eventStatusId);
}
