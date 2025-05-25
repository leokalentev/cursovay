package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.JWTUtils;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Map;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    private String createUserAndGetToken() {
        String email = faker.internet().emailAddress();
        String rawPassword = faker.internet().password(3, 12);
        User user = new User();
        user.setEmail(email);
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
        return jwtUtils.generateToken(email);
    }

    @Test
    public void testWelcomePage() throws Exception {
        var result = mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThat(body).contains("Welcome to Spring!");
    }

    @Test
    public void testIndex() throws Exception {
        String token = createUserAndGetToken();

        var result = mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {
        String token = createUserAndGetToken();

        var user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);

        MvcResult mvcResult = mockMvc.perform(get("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("firstName").isEqualTo(user.getFirstName()),
                a -> a.node("lastName").isEqualTo(user.getLastName()),
                a -> a.node("email").isEqualTo(user.getEmail()),
                a -> a.node("createdAt").isNotNull()
        );
    }

    @Test
    public void testCreate() throws Exception {
        var firstName = faker.name().firstName();
        var lastName = faker.name().lastName();
        var email = faker.internet().emailAddress();
        var password = faker.internet().password(3, 12);

        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName(firstName);
        userCreateDTO.setLastName(lastName);
        userCreateDTO.setEmail(email);
        userCreateDTO.setPassword(password);

        String token = createUserAndGetToken();

        MvcResult mvcResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(om.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("firstName").isEqualTo(firstName),
                a -> a.node("lastName").isEqualTo(lastName),
                a -> a.node("email").isEqualTo(email),
                a -> a.node("createdAt").isNotNull()
        );
    }

    @Test
    public void testUpdateUser() throws Exception {
        String rawPassword = faker.internet().password(3, 12);

        var user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail());

        String newFirstName = faker.name().firstName();
        String neEmail = faker.internet().emailAddress();

        UserUpdateDTO newUser = new UserUpdateDTO();
        newUser.setFirstName(JsonNullable.of(newFirstName));
        newUser.setEmail(JsonNullable.of(neEmail));
        newUser.setPassword(JsonNullable.of(rawPassword));

        MvcResult result = mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(om.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThatJson(responseBody).and(
                a -> a.node("firstName").isEqualTo(newFirstName),
                a -> a.node("email").isEqualTo(neEmail),
                a -> a.node("lastName").isEqualTo(user.getLastName())
        );
    }


    @Test
    public void testDeleteUser() throws Exception {
        String rawPassword = faker.internet().password(3, 12);

        var user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getEmail());

        mockMvc.perform(delete("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    public void testUpdateOtherUserForbidden() throws Exception {
        String ownerEmail = faker.internet().emailAddress();
        String ownerPassword = faker.internet().password(6, 12);
        User owner = new User();
        owner.setEmail(ownerEmail);
        owner.setFirstName("Owner");
        owner.setLastName("User");
        owner.setPassword(passwordEncoder.encode(ownerPassword));
        userRepository.save(owner);
        String ownerToken = jwtUtils.generateToken(owner.getEmail());

        User target = new User();
        target.setEmail(faker.internet().emailAddress());
        target.setFirstName("Target");
        target.setLastName("User");
        target.setPassword(passwordEncoder.encode(faker.internet().password(6, 12)));
        userRepository.save(target);

        Map<String, Object> updateData = Map.of("firstName", "Hacker");

        mockMvc.perform(put("/api/users/" + target.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content(om.writeValueAsString(updateData)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteOtherUserForbidden() throws Exception {
        String hackerEmail = faker.internet().emailAddress();
        String hackerPassword = faker.internet().password(6, 12);
        User hacker = new User();
        hacker.setEmail(hackerEmail);
        hacker.setFirstName("Hacker");
        hacker.setLastName("McEvil");
        hacker.setPassword(passwordEncoder.encode(hackerPassword));
        userRepository.save(hacker);
        String hackerToken = jwtUtils.generateToken(hacker.getEmail());

        User victim = new User();
        victim.setEmail(faker.internet().emailAddress());
        victim.setFirstName("Victim");
        victim.setLastName("User");
        victim.setPassword(passwordEncoder.encode(faker.internet().password(6, 12)));
        userRepository.save(victim);

        mockMvc.perform(delete("/api/users/" + victim.getId())
                        .header("Authorization", "Bearer " + hackerToken))
                .andExpect(status().isForbidden());
    }

}

