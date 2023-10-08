package rborodin.skillgram.userservice.entity;

import javax.persistence.*;
import java.util.Date;

//@Entity
public class Follow {

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

    public String getFollowerUserId() {
        return followerUserId;
    }

    public void setFollowerUserId(String followerUserId) {
        this.followerUserId = followerUserId;
    }

    public String getFollowingUserId() {
        return followingUserId;
    }

    public void setFollowingUserId(String followingUserId) {
        this.followingUserId = followingUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
