package Product_Management.Springboot_1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String name;
    private int quantity;
    private double price;
    private Long categoryId;
    private String categoryName;






    public ProductDto(Long id, String name, int quantity, double price, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
