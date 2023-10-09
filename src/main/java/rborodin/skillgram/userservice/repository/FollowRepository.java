package rborodin.skillgram.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.entity.FollowId;
import rborodin.skillgram.userservice.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {


    List<Follow> findByFollowerUserId(UUID uuid);
}
