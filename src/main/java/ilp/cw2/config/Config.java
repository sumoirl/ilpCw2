package ilp.cw2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public String ilpEndpoint(){
        return System.getenv().getOrDefault("ILP_ENDPOINT", "https://ilp-2025-marking.azurewebsites.net/");
    }

}
