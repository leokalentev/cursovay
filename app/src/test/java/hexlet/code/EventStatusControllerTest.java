package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.EventStatusCreateDTO;
import hexlet.code.dto.EventStatusUpdateDTO;
import hexlet.code.model.EventStatus;
import hexlet.code.model.User;
import hexlet.code.repository.EventStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.JWTUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@ActiveProfiles("test")
class EventStatusControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EventStatusRepository repository;
    @Autowired private ObjectMapper om;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTUtils jwtUtils;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    private String createUserAndGetToken() {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        String password = "password";

        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        return jwtUtils.generateToken(email);
    }

    private String generateUniqueSlug() {
        return "slug-" + UUID.randomUUID();
    }

    private EventStatus createAndSaveStatus() {
        String name = "status-" + UUID.randomUUID();
        String slug = generateUniqueSlug();

        EventStatus status = new EventStatus();
        status.setName(name);
        status.setSlug(slug);

        return repository.save(status);
    }

    @Test
    public void testIndex() throws Exception {
        String token = createUserAndGetToken();
        createAndSaveStatus();

        var result = mockMvc.perform(get("/api/eventStatuses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShowEventStatus() throws Exception {
        String token = createUserAndGetToken();
        EventStatus eventStatus = createAndSaveStatus();

        var result = mockMvc.perform(get("/api/eventStatuses/" + eventStatus.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var json = result.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("name").isEqualTo(eventStatus.getName()),
                a -> a.node("slug").isEqualTo(eventStatus.getSlug())
        );
    }

    @Test
    public void testPostEventStatus() throws Exception {
        String token = createUserAndGetToken();

        String name = "status-" + UUID.randomUUID();
        String slug = generateUniqueSlug();

        var dto = new EventStatusCreateDTO();
        dto.setName(name);
        dto.setSlug(slug);

        var result = mockMvc.perform(post("/api/eventStatuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        var json = result.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("name").isEqualTo(name),
                a -> a.node("slug").isEqualTo(slug)
        );
    }

    @Test
    public void testUpdateEventStatus() throws Exception {
        String token = createUserAndGetToken();
        EventStatus eventStatus = createAndSaveStatus();

        String newName = "updated-status-" + UUID.randomUUID();
        String newSlug = generateUniqueSlug();

        var dto = new EventStatusUpdateDTO();
        dto.setName(JsonNullable.of(newName));
        dto.setSlug(JsonNullable.of(newSlug));

        var result = mockMvc.perform(put("/api/eventStatuses/" + eventStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        var json = result.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("name").isEqualTo(newName),
                a -> a.node("slug").isEqualTo(newSlug)
        );
    }

    @Test
    public void testDeleteEventStatus() throws Exception {
        String token = createUserAndGetToken();
        EventStatus eventStatus = createAndSaveStatus();

        mockMvc.perform(delete("/api/eventStatuses/" + eventStatus.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(eventStatus.getId())).isEmpty();
    }
}



