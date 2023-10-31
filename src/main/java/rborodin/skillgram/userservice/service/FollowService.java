package rborodin.skillgram.userservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.entity.FollowId;
import rborodin.skillgram.userservice.entity.User;
import rborodin.skillgram.userservice.repository.FollowRepository;
import rborodin.skillgram.userservice.repository.UserRepository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;


    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public String createFollow(UUID id, UUID followingUserId) {
        User follower = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        User following = userRepository.findById(followingUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (following.getDeleted() == Boolean.TRUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("Невозможно подписаться на пользователя с id %s, так как страница была удалена",followingUserId));
        } else {
            Follow follow =new Follow(follower, following);
            follow.setCreatedAt(Date.from(Instant.now()));
            Follow savedFollow = followRepository.save(follow);
            return String.format("Подписка пользователля с id %s на пользователя с id %s добавлена в базу", savedFollow.getFollowerUser().getId(), savedFollow.getFollowingUser().getId());
        }
    }

    public String deleteFollow(UUID followerUserId, UUID followingUserId) {
        if (followRepository.existsById(new FollowId(followerUserId, followingUserId))) {
            followRepository.deleteById(new FollowId(followerUserId, followingUserId));
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return String.format("Подписка пользователля с id %s на пользователя с id %s удалена", followerUserId, followingUserId);
    }

    public List<Follow> findAllByUserId(UUID uuid) {
        if (userRepository.existsById(uuid)) {
            return followRepository.findByFollowerUserId(uuid);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
