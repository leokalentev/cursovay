package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.EventCreateDTO;
import hexlet.code.dto.EventUpdateDTO;
import hexlet.code.model.Category;
import hexlet.code.model.Event;
import hexlet.code.model.EventStatus;
import hexlet.code.model.User;
import hexlet.code.repository.CategoryRepository;
import hexlet.code.repository.EventRepository;
import hexlet.code.repository.EventStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.JWTUtils;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private EventRepository repository;
    @Autowired private ObjectMapper om;
    @Autowired private Faker faker;
    @Autowired private UserRepository userRepository;
    @Autowired private EventStatusRepository eventStatusRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTUtils jwtUtils;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        eventStatusRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String createUserAndGetToken() {
        String emailPrefix = faker.internet().emailAddress().split("@")[0];
        String email = emailPrefix + "@kpfu.ru";
        String rawPassword = faker.internet().password(3, 12);
        User user = new User();
        user.setEmail(email);
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        return jwtUtils.generateToken(email);
    }

    private User createAndSaveUser() {
        String emailPrefix = faker.internet().emailAddress().split("@")[0];
        String email = emailPrefix + "@kpfu.ru";
        String rawPassword = faker.internet().password(3, 12);
        User user = new User();
        user.setEmail(email);
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        return user;
    }

    private EventStatus createAndSaveStatus() {
        EventStatus s = new EventStatus();
        s.setName(faker.lorem().characters(3, 10));
        s.setSlug(faker.lorem().characters(8, 12).toLowerCase());
        return eventStatusRepository.save(s);
    }

    private Category createAndSaveCategory() {
        Category c = new Category();
        c.setName(faker.lorem().sentence());
        return categoryRepository.save(c);
    }

    private Event createAndSaveEvent() {

        User assignee = createAndSaveUser();
        EventStatus status = createAndSaveStatus();
        Category cat = createAndSaveCategory();

        Event e = new Event();
        e.setTitle(faker.lorem().sentence());
        e.setDescription(faker.lorem().sentence());
        e.setEventDate(LocalDate.now().plusDays(1));
        e.setAssignee(assignee);
        e.setEventStatus(status);
        e.setCategory(cat);
        return repository.save(e);
    }

    private Event createAndSaveEventWithAssignee(User assignee) {
        EventStatus status = createAndSaveStatus();
        Category cat = createAndSaveCategory();

        Event e = new Event();
        e.setTitle(faker.lorem().sentence());
        e.setDescription(faker.lorem().sentence());
        e.setEventDate(LocalDate.now().plusDays(1));
        e.setAssignee(assignee);
        e.setEventStatus(status);
        e.setCategory(cat);
        return repository.save(e);
    }
    @Test
    public void index() throws Exception {
        String token = createUserAndGetToken();
        createAndSaveEvent();

        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void filterByTitle() throws Exception {
        String token = createUserAndGetToken();
        Event e = createAndSaveEvent();

        mockMvc.perform(get("/api/events")
                        .param("title", e.getTitle())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(e.getId()));
    }

    @Test
    public void filterByAssignee() throws Exception {
        String token = createUserAndGetToken();
        Event e = createAndSaveEvent();

        mockMvc.perform(get("/api/events")
                        .param("assigneeId", e.getAssignee().getId().toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].assignee_id").value(e.getAssignee().getId()));
    }

    @Test
    public void filterByStatus() throws Exception {
        String token = createUserAndGetToken();
        Event e = createAndSaveEvent();

        mockMvc.perform(get("/api/events")
                        .param("status", e.getEventStatus().getSlug())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(e.getEventStatus().getSlug()));
    }

    @Test
    public void filterByCategory() throws Exception {
        String token = createUserAndGetToken();
        Event e = createAndSaveEvent();
        Long catId = e.getCategory().getId();

        mockMvc.perform(get("/api/events")
                        .param("categoryId", catId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category_id").value(catId));
    }

    @Test
    public void show() throws Exception {
        String token = createUserAndGetToken();
        Event e = createAndSaveEvent();

        MvcResult mvcResult = mockMvc.perform(get("/api/events/{id}", e.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("title").isEqualTo(e.getTitle()),
                a -> a.node("description").isEqualTo(e.getDescription()),
                a -> a.node("eventDate").isNotNull(),
                a -> a.node("status").isEqualTo(e.getEventStatus().getSlug()),
                a -> a.node("assignee_id").isEqualTo(e.getAssignee().getId()),
                a -> a.node("category_id").isEqualTo(e.getCategory().getId())
        );
    }

    @Test
    public void createEvent() throws Exception {
        String token = createUserAndGetToken();
        User assignee = createAndSaveUser();
        EventStatus status = createAndSaveStatus();
        Category cat = createAndSaveCategory();

        EventCreateDTO dto = new EventCreateDTO();
        dto.setTitle(faker.lorem().sentence());
        dto.setDescription(faker.lorem().sentence());
        dto.setEventDate(LocalDate.now().plusDays(2).toString());
        dto.setAssigneeId(assignee.getId());
        dto.setStatus(status.getSlug());
        dto.setCategoryId(cat.getId());

        MvcResult mvcResult = mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("title").isEqualTo(dto.getTitle()),
                a -> a.node("description").isEqualTo(dto.getDescription()),
                a -> a.node("eventDate").isNotNull(),
                a -> a.node("status").isEqualTo(dto.getStatus()),
                a -> a.node("assignee_id").isNotNull(),
                a -> a.node("category_id").isEqualTo(dto.getCategoryId())
        );
    }

    @Test
    public void updateEvent() throws Exception {
        User assignee = createAndSaveUser();
        String token = jwtUtils.generateToken(assignee.getEmail());
        Event e = createAndSaveEventWithAssignee(assignee);
        User newAssignee = createAndSaveUser();
        EventStatus newStatus = createAndSaveStatus();
        Category newCat = createAndSaveCategory();

        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setTitle(JsonNullable.of("Новый заголовок"));
        dto.setDescription(JsonNullable.of("Новый текст"));
        dto.setEventDate(JsonNullable.of(LocalDate.now().plusDays(5).toString()));
        dto.setAssigneeId(JsonNullable.of(newAssignee.getId()));
        dto.setStatus(JsonNullable.of(newStatus.getSlug()));
        dto.setCategoryId(JsonNullable.of((newCat.getId())));

        MvcResult mvcResult = mockMvc.perform(put("/api/events/{id}", e.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("title").isEqualTo(dto.getTitle().get()),
                a -> a.node("description").isEqualTo(dto.getDescription().get()),
                a -> a.node("eventDate").isNotNull(),
                a -> a.node("status").isEqualTo(dto.getStatus().get()),
                a -> a.node("assignee_id").isEqualTo(dto.getAssigneeId().get()),
                a -> a.node("category_id").isEqualTo(dto.getCategoryId().get())
        );
    }

    @Test
    public void deleteEvent() throws Exception {
        User assignee = createAndSaveUser();
        String token = jwtUtils.generateToken(assignee.getEmail());
        Event e = createAndSaveEventWithAssignee(assignee);

        mockMvc.perform(delete("/api/events/{id}", e.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(e.getId())).isEmpty();
    }

}


