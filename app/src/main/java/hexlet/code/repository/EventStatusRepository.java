package hexlet.code.repository;

import hexlet.code.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Long> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    Optional<EventStatus> findBySlug(String slug);
}
