package rborodin.skillgram.userservice.service;

import org.springframework.stereotype.Service;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.repository.FollowRepository;


@Service
public class FollowService {

    private final FollowRepository followRepository;


    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public String createFollow(Follow follow) {
        Follow savedFollow = followRepository.save(follow);
        return String.format("Подписка пользователля с id %s на пользователя с id %s добавлена в базу", savedFollow.getFollowerUserId(), savedFollow.getFollowingUserId());
    }
}
