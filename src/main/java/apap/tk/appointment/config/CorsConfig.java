package apap.tk.appointment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173","http://localhost:8086", "https://apap-2207.cs.ui.ac.id", "https://apap-2206.cs.ui.ac.id", "https://apap-2201.cs.ui.ac.id", "https://apap-2204.cs.ui.ac.id", "https://apap-2203.cs.ui.ac.id", "https://apap-2205.cs.ui.ac.id")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
