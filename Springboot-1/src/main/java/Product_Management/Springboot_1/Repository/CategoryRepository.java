package Product_Management.Springboot_1.Repository;

import Product_Management.Springboot_1.entity.Category;
import Product_Management.Springboot_1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String name);
}
