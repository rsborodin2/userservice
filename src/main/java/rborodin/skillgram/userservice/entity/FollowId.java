package rborodin.skillgram.userservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Table(name = "follows", schema = "users_scheme")
public class FollowId implements Serializable {

    @Column(name = "follower_user_id")
    private UUID followerUserId;

    @Column(name = "following_user_id")
    private UUID followingUserId;

}