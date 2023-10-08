package rborodin.skillgram.userservice.entity;

import javax.persistence.*;
import java.util.Date;

//@Entity
public class Follows {

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "follower_user_id")
    private String followerUserId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "following_user_id")
    private String followingUserId;

    @Column(name = "created_at")
    private Date createdAt;
}
