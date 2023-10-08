package rborodin.skillgram.userservice.repository;

import org.springframework.data.repository.CrudRepository;
import rborodin.skillgram.userservice.entity.Follow;
import rborodin.skillgram.userservice.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface FollowRepository extends CrudRepository<Follow, Long> {

    Optional<User> findById(UUID uuid);

    void deleteById(UUID uuid);

    boolean existsById(UUID id);
}
