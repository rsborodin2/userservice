package rborodin.skillgram.userservice.module_tests.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.helper.JsonHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс для модульного тестирования раздела пользователей
 */
@Transactional
@AutoConfigureMockMvc()
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    List<User> users;
    List<User> usersCreated;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    /**
     * Настройка Testcontainer Postgres
     * @param registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();

    }

    /**
     * Создание и инициализация тестовых данных
     * @throws Exception
     */
    @BeforeEach
    void beforeEach() throws Exception {

        // Добавление тестовых пользователей
        users = Instancio.ofList(User.class).size(10)
                .ignore(field(User::getId))
                .set(field(User::getDeleted), false)
                .create();
        users.forEach(x -> {
            try {
                mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(x))
                        .accept(MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        usersCreated = JsonHelper.parseJsonArray(mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), User.class);
    }


    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @Test
    void contextLoads() {
    }


    @Test
    @DisplayName("Пользователь успешно создан -> получаем ответ 200; выполнено добавление пользователя в БД")
    void createUserSuccessful() throws Exception {
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getDeleted), false)
                .create();
        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertTrue(result.contains("Пользователь " + user.getSurname() + " добавлен в базу с id = "));

        String resultGet = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(JsonHelper.parseJsonArray(resultGet, User.class).size(), users.size() + 1);
    }

    @Test
    @DisplayName("Неудачное создание пользователя -> получаем ответ 400; пользователь не добавлен в БД")
    void createUserError() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("error")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String resultGet = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(JsonHelper.parseJsonArray(resultGet, User.class).size(), users.size());
    }

    @Test
    @DisplayName("Пользователь успешно найден по полю ID -> получаем ответ 200")
    void findByIdSuccessful() throws Exception {
        User userToFind = usersCreated.get(0);
        String resultGet = mockMvc.perform(get("/users/" + userToFind.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(usersCreated.get(0), JsonHelper.fromJson(User.class, resultGet));
    }

    @Test
    @DisplayName("Пользователь не найден по полю ID -> получаем ответ 404")
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Пользователь не найден по полю ID -> получаем ответ 404; запись не обновлена в БД")
    void updateUserNotFound() throws Exception {
        User userToUpdate = usersCreated.get(0);
        userToUpdate.setFirstname("Новое_имя");
        mockMvc.perform(put("/users/" + UUID.randomUUID())
                        .content(JsonHelper.toJson(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        String resultGet = mockMvc.perform(get("/users/" + userToUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertNotEquals(usersCreated.get(0), requireNonNull(JsonHelper.fromJson(User.class, resultGet)));

    }

    @Test
    @DisplayName("Пользователь успешно обновлен -> получаем ответ 200; обновлена запись в БД")
    void updateUserSuccessful() throws Exception {
        User userToUpdate = usersCreated.get(0);
        userToUpdate.setFirstname("Новое_имя");
        String result = mockMvc.perform(put("/users/" + usersCreated.get(0).getId())
                        .content(JsonHelper.toJson(userToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(String.format("Пользователь с id=%s успешно сохранен", userToUpdate.getId().toString()), result);

        String resultGet = mockMvc.perform(get("/users/" + userToUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(JsonHelper.fromJson(User.class, resultGet).getFirstname(), "Новое_имя");

    }

    @Test
    @DisplayName("Пользователь успешно удален -> получаем ответ 200; выполнено удаление пользователя в БД")
    void deleteUserSuccessful() throws Exception {
        User userDelete = usersCreated.get(0);
        String resultDelete = mockMvc.perform(delete("/users/" + userDelete.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(String.format("Пользователь с id=%s успешно удален", userDelete.getId()), resultDelete);

        String resultGet = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(JsonHelper.parseJsonArray(resultGet, User.class).size(), users.size() - 1);
    }

    @Test
    @DisplayName("Пользователь не найден по ID -> получаем ответ 200; запись в БД не удалена")
    void deleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        String resultGet = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(JsonHelper.parseJsonArray(resultGet, User.class).size(), users.size());
    }

    @Test
    @DisplayName("Все пользователи успешно найдены -> получаем ответ 200")
    void findAllSuccessful() throws Exception {
        String result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(JsonHelper.parseJsonArray(result, User.class).size(), users.size());
    }
}