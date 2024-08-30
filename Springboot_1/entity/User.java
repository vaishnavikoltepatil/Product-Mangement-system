package Product_Management.Springboot_1.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.processing.SQL;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
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

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE; // default value



    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="user_products", joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products;


    // Constructor with parameters
    public User(String username, String email, Set<Product> products) {
        this.username = username;
        this.email = email;
        this.products = products;
        this.deleted = false;
    }


}

