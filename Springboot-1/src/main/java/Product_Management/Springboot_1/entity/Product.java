package Product_Management.Springboot_1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name="product_name")
    private String name;

    private int quantity;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private Set<User> users;


    // Constructor with parameters
    public Product(String name, int quantity, double price, Category category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;

    }
}
