package rborodin.skillgram.userservice.controller;

import org.springframework.web.bind.annotation.*;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.service.UserService;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    String createUsers(@RequestBody User user){
       return userService.createUser(user);
    }

    @GetMapping("/{id}")
    User getUserById(@PathVariable("id") UUID id){
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    String updateUser(@RequestBody User user, @PathVariable UUID id){
        return userService.updateUser(user,id);
    }

    @DeleteMapping("/{id}")
    String deleteUser(@PathVariable UUID id){
        return userService.deleteUser(id);
    }

    @GetMapping
    Iterable<User> findAll(){
        return StreamSupport.stream(userService.findAll().spliterator(), false)
                .filter(x->!x.getDeleted())
                .collect(Collectors.toList());
    }

}
