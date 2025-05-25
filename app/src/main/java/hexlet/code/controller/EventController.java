package hexlet.code.controller;

import hexlet.code.dto.EventCreateDTO;
import hexlet.code.dto.EventDTO;
import hexlet.code.dto.EventUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.EventMapper;
import hexlet.code.repository.EventRepository;
import hexlet.code.specification.EventSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/api")
@Validated
public class EventController {
    @Autowired
    private EventRepository repository;

    @Autowired
    private EventMapper mapper;

    @Autowired
    private EventSpecification specBuilder;

    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> index(EventDTO params) {
        var spec = specBuilder.build(params);
        var events = repository.findAll(spec);
        var res = events.stream().map(mapper::map).toList();
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(events.size()))
                .body(res);
    }

    @GetMapping("/events/{id}")
    public EventDTO show(@PathVariable Long id) {
        var event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return mapper.map(event);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO create(@RequestBody @Valid EventCreateDTO dto) {
        var event = mapper.map(dto);
        repository.save(event);
        return mapper.map(event);
    }

    @PutMapping("/events/{id}")
    public EventDTO update(@PathVariable Long id, @RequestBody EventUpdateDTO dto) {
        var event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!dto.getTitle().isPresent() || dto.getTitle().get().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле title обязательно и не может быть пустым");
        }
        if (!dto.getStatus().isPresent() || dto.getStatus().get().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле status обязательно и не может быть пустым");
        }

        mapper.update(dto, event);
        repository.save(event);
        return mapper.map(event);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        var event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        repository.delete(event);
    }
}
