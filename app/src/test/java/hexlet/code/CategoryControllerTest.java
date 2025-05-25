package hexlet.code;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.CategoryCreateDTO;
import hexlet.code.dto.CategoryUpdateDTO;
import hexlet.code.model.Category;
import hexlet.code.model.User;
import hexlet.code.repository.CategoryRepository;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private CategoryRepository repository;
    @Autowired private ObjectMapper om;
    @Autowired private Faker faker;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTUtils jwtUtils;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    private String createUserAndGetToken() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);
        User user = new User();
        user.setEmail(email);
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return jwtUtils.generateToken(email);
    }

    private Category createAndSaveCategory() {
        String name = faker.lorem().sentence();
        Category category = new Category();
        category.setName(name);
        repository.save(category);

        return category;
    }

    @Test
    public void index() throws Exception {
        String token = createUserAndGetToken();
        createAndSaveCategory();

        var result = mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void showCategory() throws Exception {
        String token = createUserAndGetToken();

        String name = faker.lorem().sentence();
        Category category = new Category();
        category.setName(name);
        repository.save(category);

        MvcResult mvcResult = mockMvc.perform(get("/api/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("name").isEqualTo(category.getName()),
                a -> a.node("createdAt").isNotNull()
        );
    }

    @Test
    public void createCategory() throws Exception {
        String token = createUserAndGetToken();

        String name = faker.lorem().sentence();
        CategoryCreateDTO category = new CategoryCreateDTO();
        category.setName(name);

        MvcResult mvcResult = mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(om.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andReturn();
        var json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("name").isEqualTo(name)
        );
    }

    @Test
    public void updateCategory() throws Exception {
        String token = createUserAndGetToken();

        String name = faker.lorem().sentence();
        Category category = new Category();
        category.setName(name);
        repository.save(category);

        String newName = faker.lorem().sentence();
        CategoryUpdateDTO newCategory = new CategoryUpdateDTO();
        newCategory.setName(JsonNullable.of(newName));

        MvcResult mvcResult = mockMvc.perform(put("/api/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(om.writeValueAsString(newCategory)))
                .andExpect(status().isOk())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        assertThatJson(json).and(
                a -> a.node("name").isEqualTo(newName)
        );
    }

    @Test
    public void deleteCategory() throws Exception {
        String token = createUserAndGetToken();

        String name = faker.lorem().sentence();
        Category category = new Category();
        category.setName(name);
        repository.save(category);

        mockMvc.perform(delete("/api/categories/" + category.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(category.getId())).isEmpty();
    }
}
