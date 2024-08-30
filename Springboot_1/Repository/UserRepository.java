package Product_Management.Springboot_1.Repository;


import Product_Management.Springboot_1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByDeletedFalse(); // Add method to find all non-deleted users

    Optional<User> findByIdAndDeletedFalse(Long id); // Add method to find non-deleted user by ID

}
