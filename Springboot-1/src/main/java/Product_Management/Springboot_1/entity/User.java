package Product_Management.Springboot_1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String username;

//    @Column(name="email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(name="user_products",joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products;



    // Constructor with parameters
    public User(String username, String email, Set<Product> products) {
        this.username = username;
        this.email=email;
        this.products = products;
    }
}
