package hexlet.code.controller;

import hexlet.code.dto.EventStatusCreateDTO;
import hexlet.code.dto.EventStatusDTO;
import hexlet.code.dto.EventStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.EventStatusMapper;
import hexlet.code.repository.EventRepository;
import hexlet.code.repository.EventStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class EventStatusController {
    @Autowired
    private EventStatusRepository repository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventStatusMapper mapper;

    @GetMapping("/eventStatuses")
    public ResponseEntity<List<EventStatusDTO>> index() {
        var statuses = repository.findAll();
        var res = statuses.stream().map(mapper::map).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.size()))
                .body(res);
    }

    @GetMapping("/eventStatuses/{id}")
    public EventStatusDTO show(@PathVariable Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event status not found"));
        return mapper.map(status);
    }

    @PostMapping("/eventStatuses")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public EventStatusDTO create(@RequestBody @Valid EventStatusCreateDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус с таким названием уже существует");
        }
        if (repository.existsBySlug(dto.getSlug())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус с таким слагом уже существует");
        }
        var status = mapper.map(dto);
        repository.save(status);
        return mapper.map(status);
    }

    @PutMapping("/eventStatuses/{id}")
    @PreAuthorize("isAuthenticated()")
    public EventStatusDTO update(@PathVariable Long id, @RequestBody EventStatusUpdateDTO dto) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event status not found"));

        if (!dto.getName().isPresent() || dto.getName().get().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле name обязательно и не может быть пустым");
        }
        if (!dto.getSlug().isPresent() || dto.getSlug().get().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле slug обязательно и не может быть пустым");
        }
        if (repository.existsByName(dto.getName().get()) && !dto.getName().get().equals(status.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус с таким названием уже существует");
        }
        if (repository.existsBySlug(dto.getSlug().get()) && !dto.getSlug().get().equals(status.getSlug())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус с таким слагом уже существует");
        }

        mapper.update(dto, status);
        repository.save(status);
        return mapper.map(status);
    }

    @DeleteMapping("/eventStatuses/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event status not found"));

        if (eventRepository.existsByEventStatusId(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя удалить статус, связанный с событием");
        }
        repository.delete(status);
    }
}

