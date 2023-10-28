package rborodin.skillgram.userservice.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import rborodin.skillgram.userservice.UserserviceApplication;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.helper.JsonHelper;
import rborodin.skillgram.userservice.repository.FollowRepository;
import rborodin.skillgram.userservice.repository.UserRepository;
import rborodin.skillgram.userservice.service.FollowService;
import rborodin.skillgram.userservice.service.UserService;

import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest

@Testcontainers
@EnableJpaRepositories(basePackages = "rborodin.skillgram.userservice.repository")
@AutoConfigureMockMvc()
class UserControllerTest {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll(){
        postgres.stop();
    }


    @Test
    void contextLoads() {
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",postgres::getJdbcUrl);
        registry.add("spring.datasource.username",postgres::getUsername);
        registry.add("spring.datasource.password",postgres::getPassword);

    }


    @Autowired
    static MockMvc mockMvc;
    @Autowired
    static UserRepository userRepository;
    @Autowired
    static UserService userService;

    @BeforeEach
    private void init() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);

    }


    @Test
    void createUser() throws Exception {
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getDeleted), false)
                .create();
        User createdUser = user;
        createdUser.setId(UUID.randomUUID());
        Mockito.when(userRepository.save(user)).thenReturn(createdUser);

        Mockito.when(userService.createUser(user)).thenReturn("Пользователь " + user.getSurname() + " добавлен в базу с id = " + user.getId().toString());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getUserById() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void findAll() {
    }
}