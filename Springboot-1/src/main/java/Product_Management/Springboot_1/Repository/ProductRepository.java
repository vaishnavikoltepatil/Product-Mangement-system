package Product_Management.Springboot_1.Repository;

import Product_Management.Springboot_1.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    List<Product> findAll(Sort sort);


    // Custom query to fetch all products sorted by id in descending order
    @Query("SELECT p FROM Product p ORDER BY p.id DESC")
    List<Product> findAllSortedByIdDesc();
}
