package rborodin.skillgram.userservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.service.FollowService;

@RestController
@RequestMapping(value = "/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    String createFollow(@RequestBody Follow follow){
        return followService.createFollow(follow);
    }

}
