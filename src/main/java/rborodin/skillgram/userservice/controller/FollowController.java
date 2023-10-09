package rborodin.skillgram.userservice.controller;

import org.springframework.web.bind.annotation.*;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.entity.FollowId;
import rborodin.skillgram.userservice.service.FollowService;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "users/{id}/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    String createFollow(@PathVariable UUID id,@RequestParam UUID followingUserId){
        return followService.createFollow(id,followingUserId);
    }

    @DeleteMapping
    String deleteFollow(@PathVariable UUID id,@RequestParam UUID followingUserId){
        return followService.deleteFollow(id,followingUserId);
    }

    @GetMapping
    List<Follow> findFollows(@PathVariable UUID id){
        return followService.findAllByUserId(id);
    }

}
