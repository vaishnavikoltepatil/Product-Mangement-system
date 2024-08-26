package Product_Management.Springboot_1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    private String username;

//    @Column(name="email")
    private String email;

    private String password;
    private boolean active;
    private String otp;
    private LocalDateTime otpGeneratedTime;


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
