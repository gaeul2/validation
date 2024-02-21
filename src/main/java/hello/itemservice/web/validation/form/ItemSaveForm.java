package hello.itemservice.web.validation.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Data
public class ItemSaveForm {
    @NotBlank
    private String itemName;
    @NotNull
    @Range(min=1000, max=100000)
    private Integer price;

    @NotNull
    @Max(value = 9999)//등록시에만 체크
    private Integer quantity;

}
