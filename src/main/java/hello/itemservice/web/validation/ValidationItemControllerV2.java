package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item,
                          BindingResult bindingResult, //순서가 중요 얘는 @ModelAttribute 다음에 와야함! 그래야 item의 에러를 담을 수 있음
                          RedirectAttributes redirectAttributes,
                          Model model) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){ //item.ItemName에 글자가 없으면
            //오브젝트명(@ModelAttribute에 담기는 이름을 넣어주면 됨, 필드명, 디폴트 메세지.
            bindingResult.addError(new FieldError("item","itemName", "상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){ //가격이 없거나 천원보다 작거나 백만원보다 크면
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000){
                //field에러가 아닌 글로벌 에러를 처리하기위해 ObjectError 객체사용
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력폼으로
        if (bindingResult.hasErrors()){ //errors맵이 빈값이 아니면 오류가 있다는 뜻이지.
            log.info("errors={}", bindingResult);
            //bindingResult는 자동으로 view에 넘어감. 그래서 model.addAttribute 안해줘도됨
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item,
                            BindingResult bindingResult, //순서가 중요 얘는 @ModelAttribute 다음에 와야함! 그래야 item의 에러를 담을 수 있음
                            RedirectAttributes redirectAttributes,
                            Model model) {

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){ //item.ItemName에 글자가 없으면
            //오브젝트명(@ModelAttribute에 담기는 이름을 넣어주면 됨, 필드명, 디폴트 메세지.
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false, null, null,"상품 이름은 필수입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){ //가격이 없거나 천원보다 작거나 백만원보다 크면
            bindingResult.addError(new FieldError("item","price", item.getPrice(), false, null, null,"가격은 1,000 ~ 1,000,000까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item","quantity", item.getQuantity(), false, null, null,"수량은 최대 9,999 까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000){
                //field에러가 아닌 글로벌 에러를 처리하기위해 ObjectError 객체사용
                bindingResult.addError(new ObjectError("item", null,null,"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            }
        }

        //검증에 실패하면 다시 입력폼으로
        if (bindingResult.hasErrors()){ //errors맵이 빈값이 아니면 오류가 있다는 뜻이지.
            log.info("errors={}", bindingResult);
            //bindingResult는 자동으로 view에 넘어감. 그래서 model.addAttribute 안해줘도됨
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item,
                            BindingResult bindingResult, //순서가 중요 얘는 @ModelAttribute 다음에 와야함! 그래야 item의 에러를 담을 수 있음
                            RedirectAttributes redirectAttributes,
                            Model model) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){ //item.ItemName에 글자가 없으면
            //오브젝트명(@ModelAttribute에 담기는 이름을 넣어주면 됨, 필드명, 디폴트 메세지.
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null,null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){ //가격이 없거나 천원보다 작거나 백만원보다 크면
            bindingResult.addError(new FieldError("item","price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000},null));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item","quantity", item.getQuantity(), false, new String[]{"max.item.quantity="}, new Object[]{9999},null));
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000){
                //field에러가 아닌 글로벌 에러를 처리하기위해 ObjectError 객체사용
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"},new Object[]{10000, resultPrice},null));
            }
        }

        //검증에 실패하면 다시 입력폼으로
        if (bindingResult.hasErrors()){ //errors맵이 빈값이 아니면 오류가 있다는 뜻이지.
            log.info("errors={}", bindingResult);
            //bindingResult는 자동으로 view에 넘어감. 그래서 model.addAttribute 안해줘도됨
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item,
                            BindingResult bindingResult, //순서가 중요 얘는 @ModelAttribute 다음에 와야함! 그래야 item의 에러를 담을 수 있음
                            RedirectAttributes redirectAttributes,
                            Model model) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        //검증 로직
        if(!StringUtils.hasText(item.getItemName())){ //item.ItemName에 글자가 없으면
            //오브젝트명(@ModelAttribute에 담기는 이름을 넣어주면 됨, 필드명, 디폴트 메세지.
            bindingResult.rejectValue("itemName", "required");
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){ //가격이 없거나 천원보다 작거나 백만원보다 크면
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000){
                //field에러가 아닌 글로벌 에러를 처리하기위해 ObjectError 객체사용
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력폼으로
        if (bindingResult.hasErrors()){ //errors맵이 빈값이 아니면 오류가 있다는 뜻이지.
            log.info("errors={}", bindingResult);
            //bindingResult는 자동으로 view에 넘어감. 그래서 model.addAttribute 안해줘도됨
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item,
                            BindingResult bindingResult, //순서가 중요 얘는 @ModelAttribute 다음에 와야함! 그래야 item의 에러를 담을 수 있음
                            RedirectAttributes redirectAttributes,
                            Model model) {

        //이건 생략
        //itemValidator.supports(item.getClass());

        itemValidator.validate(item, bindingResult);

        //검증에 실패하면 다시 입력폼으로
        if (bindingResult.hasErrors()){ //errors맵이 빈값이 아니면 오류가 있다는 뜻이지.
            log.info("errors={}", bindingResult);
            //bindingResult는 자동으로 view에 넘어감. 그래서 model.addAttribute 안해줘도됨
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item,
                            BindingResult bindingResult, //순서가 중요 얘는 @ModelAttribute 다음에 와야함! 그래야 item의 에러를 담을 수 있음
                            RedirectAttributes redirectAttributes,
                            Model model) {

        //@Validated 넣으면 Item에 대해서 알아서 검증기가 수행됨
        //이 어노테이션 자체가 검증기를 실행하라 라는 어노테이션임. -> 얘가 작동하려면 컨트롤러위에 @InitBinder가 있다는 전제하임.


        //검증에 실패하면 다시 입력폼으로
        if (bindingResult.hasErrors()){ //errors맵이 빈값이 아니면 오류가 있다는 뜻이지.
            log.info("errors={}", bindingResult);
            //bindingResult는 자동으로 view에 넘어감. 그래서 model.addAttribute 안해줘도됨
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

