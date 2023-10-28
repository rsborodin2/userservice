package rborodin.skillgram.userservice.unit_tests.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.entity.FollowId;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.repository.FollowRepository;
import rborodin.skillgram.userservice.repository.UserRepository;
import rborodin.skillgram.userservice.service.FollowService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class FollowServiceTest {
    static UserRepository userRepository;
    static FollowRepository followRepository;
    static FollowService followService;

    @BeforeEach
    private void init() {
        userRepository = Mockito.mock(UserRepository.class);
        followRepository = Mockito.mock(FollowRepository.class);
        followService = new FollowService(followRepository, userRepository);
    }

    @Test
    void createFollowSuccess() {
        //given
        Follow follow = Instancio.of(Follow.class).create();
        Mockito.when(userRepository.findById(any(UUID.class)))
                .thenReturn(Optional.ofNullable(Instancio.of(User.class)
                .set(field(User::getDeleted), false)
                .create()));
        Mockito.when(followRepository.save(any(Follow.class))).thenReturn(follow);


        //when
        String result = followService.createFollow(follow.getFollowerUser().getId(), follow.getFollowingUser().getId());

        //then
        Assertions.assertEquals(String.format("Подписка пользователля с id %s на пользователя с id %s добавлена в базу", follow.getFollowerUser().getId(), follow.getFollowingUser().getId()), result);

    }

    @Test
    void createFollowFollowingDeletedBadRequest() {
        //given
        Follow follow = Instancio.of(Follow.class).create();
        Mockito.when(userRepository.findById(follow.getFollowerUser().getId()))
                .thenReturn(Optional.ofNullable(Instancio.of(User.class)
                        .set(field(User::getDeleted), false)
                        .create()));
        Mockito.when(userRepository.findById(follow.getFollowingUser().getId()))
                .thenReturn(Optional.ofNullable(Instancio.of(User.class)
                        .set(field(User::getDeleted), true)
                        .create()));
        Mockito.when(followRepository.save(any(Follow.class))).thenReturn(follow);

        //when
        Executable executable = ()-> String.valueOf(followService.createFollow(follow.getFollowerUser().getId(), follow.getFollowingUser().getId()));

        //then
        Assertions.assertThrows(ResponseStatusException.class, executable);
    }

    @Test
    void deleteFollowSuccess() {
        //given
        Follow follow = Instancio.of(Follow.class).create();
        Mockito.when(followRepository.existsById(any(FollowId.class))).thenReturn(true);

        //when
        String result = followService.deleteFollow(follow.getFollowerUser().getId(), follow.getFollowingUser().getId());

        //then
        Assertions.assertEquals(String.format("Подписка пользователля с id %s на пользователя с id %s удалена", follow.getFollowerUser().getId(), follow.getFollowingUser().getId()), result);
    }

    @Test
    void deleteFollowNotFound() {
        //given
        Follow follow = Instancio.of(Follow.class).create();
        Mockito.when(followRepository.existsById(any(FollowId.class))).thenReturn(false);

        //when
        Executable executable = ()-> String.valueOf(followService.deleteFollow(follow.getFollowerUser().getId(), follow.getFollowingUser().getId()));

        //then
        Assertions.assertThrows(ResponseStatusException.class, executable);
    }

    @Test
    void findAllByUserId() {
        //given
        List<Follow> follows = Instancio.ofList(Follow.class).create();
        Mockito.when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        Mockito.when(followRepository.findByFollowerUserId(any(UUID.class))).thenReturn(follows);

        //when
        String result = String.valueOf(followService.findAllByUserId(UUID.randomUUID()));

        //then
        Assertions.assertEquals(follows.toString(), result);
    }

    @Test
    void findAllByUserIdNotFound() {
        //given
        List<Follow> follows = Instancio.ofList(Follow.class).create();
        Mockito.when(userRepository.existsById(any(UUID.class))).thenReturn(false);
        Mockito.when(followRepository.findByFollowerUserId(any(UUID.class))).thenReturn(follows);

        //when
        Executable executable = ()-> String.valueOf(followService.findAllByUserId(UUID.randomUUID()));

        //then
        Assertions.assertThrows(ResponseStatusException.class, executable);
    }
}