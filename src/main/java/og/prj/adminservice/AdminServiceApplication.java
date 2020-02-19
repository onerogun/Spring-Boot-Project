package og.prj.adminservice;

import og.prj.adminservice.jwt.CustomAuthenticationSuccessHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@SpringBootApplication
@EnableJpaRepositories
public class AdminServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(AdminServiceApplication.class, args);
	}


/*
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/getproducts/saveimage/").allowedOrigins("http://edit-pics.s3-website-us-east-1.amazonaws.com/");
			}
		};
	}*/
}
