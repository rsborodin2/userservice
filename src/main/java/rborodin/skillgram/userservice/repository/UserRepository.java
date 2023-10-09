package rborodin.skillgram.userservice.repository;


import org.springframework.data.repository.CrudRepository;
import rborodin.skillgram.userservice.entity.User;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, Long> {
    
    Optional<User> findById(UUID uuid);
    @Transactional
    void deleteById(UUID uuid);

    boolean existsById(UUID id);
}
