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
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.helper.JsonHelper;

import java.util.List;

import static  java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.randomUUID;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rborodin.skillgram.userservice.helper.JsonHelper.parseJsonArray;

/**
 * Класс для модульного тестирования раздела подписок
 */
@Transactional
@AutoConfigureMockMvc()
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FollowControllerModuleTest {

    @Autowired
    MockMvc mockMvc;

    List<User> usersCreated;

    List<Follow> followsCreated;
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
        List<User> users = Instancio.ofList(User.class).size(10)
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

        usersCreated = parseJsonArray(mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8), User.class);


        // Добавление тестовых подписок на одного пользователя
        for (int i = 1; i < 3; i++) {
            mockMvc.perform(post("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("followingUserId", usersCreated.get(i).getId().toString())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

        }

        followsCreated = parseJsonArray(mockMvc.perform(get("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8), Follow.class);


    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("Подписка успешно создана -> получаем ответ 200; выполнено добавление подписки в БД")
    void createFollowSuccess() throws Exception {

        // проверка:: получаем ответ 200 и сообщение об успешном добавлении пользователя
        String result =  mockMvc.perform(post("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("followingUserId", usersCreated.get(3).getId().toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        Assertions.assertEquals(String.format("Подписка пользователля с id %s на пользователя с id %s добавлена в базу", usersCreated.get(0).getId(), usersCreated.get(3).getId().toString()), result);

        // проверка:: размер выборки из БД увеличен на 1 запись
        List<Follow> followsNew = parseJsonArray(mockMvc.perform(get("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8), Follow.class);
        Assertions.assertEquals(followsNew.size(), followsCreated.size() + 1);


    }

    @Test
    @DisplayName("Подписка успешно удалена -> получаем ответ 200; выполнено удаление подписки в БД")
    void deleteFollowSuccess() throws Exception {

        // проверка:: получаем ответ 200
        String result =  mockMvc.perform(delete("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("followingUserId", usersCreated.get(2).getId().toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        Assertions.assertEquals(String.format("Подписка пользователля с id %s на пользователя с id %s удалена", usersCreated.get(0).getId(), usersCreated.get(2).getId().toString()), result);

        // проверка:: размер выборки из БД уменьшен на 1 запись
        List<Follow> followsNew = parseJsonArray(mockMvc.perform(get("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8), Follow.class);

        Assertions.assertEquals(followsNew.size(), followsCreated.size() - 1);
    }

    @Test
    @DisplayName("Не найден пользователь, на кого должна быть создана подписка -> получаем ответ 404")
    void deleteFollowNotFound() throws Exception {
        mockMvc.perform(delete("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("followingUserId", randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Все подписки на пользователя успешно найдены -> получаем ответ 200")
    void findAllByUserId() throws Exception {
        List<Follow> followsNew = parseJsonArray(mockMvc.perform(get("/users/" + usersCreated.get(0).getId().toString() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8), Follow.class);

        Assertions.assertEquals(followsNew.size(), followsCreated.size());
    }

    @Test
    @DisplayName("Не найден пользователь, у кого должна быть создана подписка -> получаем ответ 404")
    void findAllByUserIdNotFound() throws Exception {
        mockMvc.perform(delete("/users/" + randomUUID() + "/follows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("followingUserId", usersCreated.get(2).getId().toString()))
                .andExpect(status().isNotFound());
    }
}
