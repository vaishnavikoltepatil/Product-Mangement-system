package Product_Management.Springboot_1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private List<ProductDto> products;




    public UserDto(Long id, String username, String email, List<ProductDto> products) {
        this.id = id;
        this.username = username;
        this.email=email;
        this.products = products;
    }

}
