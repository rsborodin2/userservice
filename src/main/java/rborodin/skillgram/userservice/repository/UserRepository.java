package rborodin.skillgram.userservice.repository;


import org.springframework.data.repository.CrudRepository;
import rborodin.skillgram.userservice.entity.User;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {


}
