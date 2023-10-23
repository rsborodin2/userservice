package rborodin.skillgram.userservice.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.repository.UserRepository;

import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;

class UserServiceTest {

    static UserRepository userRepository;
    static UserService userService;

    @BeforeEach
    private void init() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void createUserSuccess() throws ParseException {

        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getDeleted), false)
                .create();
        User createdUser = user;
        createdUser.setId(UUID.randomUUID());
        Mockito.when(userRepository.save(user)).thenReturn(createdUser);

        //when

        String result = userService.createUser(user);

        //then
        Assertions.assertEquals("Пользователь " + user.getSurname() + " добавлен в базу с id = " + createdUser.getId().toString(), result);
    }

    @Test
    void createUserError() throws ParseException {

        //given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getDeleted), false)
                .create();
        User createdUser = user;
        createdUser.setId(UUID.randomUUID());
        Mockito.when(userRepository.save(user)).thenThrow(PersistenceException.class);

        //when
        Executable executable = () -> userService.createUser(user);

        //then
        Assertions.assertThrows(PersistenceException.class, executable);
    }

    @Test
    void updateUserSuccess() {

        //given
        User user = Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        //when
        String result = userService.updateUser(user, user.getId());

        //then
        Assertions.assertEquals(String.format("Пользователь с id=%s успешно сохранен", user.getId()), result);

    }

    @Test
    void updateUserNotFound() {

        //given
        User user = Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.existsById(any(UUID.class))).thenReturn(false);
        //when
        Executable executable = ()-> String.valueOf(userService.updateUser(user,user.getId()));

        //then
        Assertions.assertThrows(ResponseStatusException.class, executable);

    }

    @Test
    void findByIdSuccess() {
        //given
        User user = Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(user));
        //when
        String result = String.valueOf(userService.findById(user.getId()));

        //then
        Assertions.assertEquals(user.toString(), result);
    }

    @Test
    void findByIdNotFound() {
        //given
        User user = Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        //when
        Executable executable = ()-> String.valueOf(userService.findById(user.getId()));

        //then
        Assertions.assertThrows(ResponseStatusException.class, executable);
    }

    @Test
    void deleteUserSuccess() {
        //given
        User user = Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        //when
        String result = userService.deleteUser(user.getId());

        //then
        Assertions.assertEquals(String.format("Пользователь с id=%s успешно удален", user.getId()), result);
    }

    @Test
    void deleteUserNotFound() {

        //given
        User user = Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.existsById(any(UUID.class))).thenReturn(false);
        //when
        Executable executable = ()-> String.valueOf(userService.deleteUser(user.getId()));

        //then
        Assertions.assertThrows(ResponseStatusException.class, executable);

    }

    @Test
    void findAll() {
        //given
        List<User> users = Instancio.ofList(User.class)
                .set(field(User::getDeleted), false)
                .create();
        Mockito.when(userRepository.findAll()).thenReturn(users);
        //when
        String result = String.valueOf(userService.findAll());

        //then
        Assertions.assertEquals(users.toString(), result);
    }
}