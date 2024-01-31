package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component //SpringBean에 등록됨.
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        //파라미터로 넘어오는 클래스가 Item클래스에서 지원이 되냐를 확인
        return Item.class.isAssignableFrom(clazz);
        //item == clazz
        //item == subItem(item의 자식클래스)
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;
        //Errors는 BindingResult의 부모 클래스

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){ //item.ItemName에 글자가 없으면
            //오브젝트명(@ModelAttribute에 담기는 이름을 넣어주면 됨, 필드명, 디폴트 메세지.
            errors.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){ //가격이 없거나 천원보다 작거나 백만원보다 크면
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000){
                //field에러가 아닌 글로벌 에러를 처리하기위해 ObjectError 객체사용
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

    }
}
