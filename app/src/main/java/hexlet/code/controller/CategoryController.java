package hexlet.code.controller;

import hexlet.code.dto.CategoryCreateDTO;
import hexlet.code.dto.CategoryDTO;
import hexlet.code.dto.CategoryUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.CategoryMapper;
import hexlet.code.repository.CategoryRepository;
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
public class CategoryController {
    @Autowired
    private CategoryRepository repository;

    @Autowired
    private CategoryMapper mapper;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> index() {
        var categories = repository.findAll();
        var res = categories.stream().map(mapper::map).toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(categories.size()))
                .body(res);
    }

    @GetMapping("/categories/{id}")
    public CategoryDTO show(@PathVariable Long id) {
        var category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return mapper.map(category);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO create(@RequestBody @Valid CategoryCreateDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория с таким названием уже существует");
        }
        var category = mapper.map(dto);
        repository.save(category);
        return mapper.map(category);
    }

    @PutMapping("/categories/{id}")
    public CategoryDTO update(@PathVariable Long id, @RequestBody CategoryUpdateDTO dto) {
        var category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!dto.getName().isPresent() || dto.getName().get().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле name обязательно и не может быть пустым");
        }
        mapper.update(dto, category);
        repository.save(category);
        return mapper.map(category);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        var category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getEvents().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Нельзя удалить категорию, связанную с событиями");
        }
        repository.delete(category);
    }
}
