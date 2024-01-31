package hello.itemservice;

import hello.itemservice.web.validation.ItemValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ItemServiceApplication {
//public class ItemServiceApplication implements WebMvcConfigurer { //1) 모든 컨트롤러에 검증기를 적용하고 싶으면

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	//1) 근데 글로벌 설정을 직접사용하는 경우는 드뭄. 이거 설정하면 BeanValidator가 자동등록되지 않음.
//	@Override
//	public Validator getValidator(){
//		return new ItemValidator();
//	}

}
