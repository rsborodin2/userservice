package rborodin.skillgram.userservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;

    public String updateUser(User user, UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.save(user);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return String.format("Пользователь с id=%s успешно сохранен", id);
    }

    public String createUser(User user) {
        User savedUser = userRepository.save(user);
        return String.format("Пользователь %s добавлен в базу с id = %s", savedUser.getSurname(), savedUser.getId());
    }

    public User findById(UUID uuid) {
        return userRepository.findById(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public String deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return String.format("Пользователь с id=%s успешно удален", id);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
}
