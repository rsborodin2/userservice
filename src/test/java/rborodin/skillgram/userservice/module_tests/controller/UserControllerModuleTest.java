package rborodin.skillgram.userservice.module_tests.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rborodin.skillgram.userservice.controller.UserController;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.helper.JsonHelper;
import rborodin.skillgram.userservice.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerModuleTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser() throws Exception {
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getDeleted), false)
                .create();
        when(userService.createUser(any(User.class))).thenReturn(String.format("Пользователь %s добавлен в базу с id = %s", user.getSurname(), user.getId()));
        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonHelper.toJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertTrue(result.contains("Пользователь " + user.getSurname() + " добавлен в базу с id = "));

    }

}
