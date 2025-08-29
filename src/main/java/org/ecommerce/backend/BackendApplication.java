package org.ecommerce.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
public class BackendApplication {
    /**
     * TODO:
     * add logging and use AOP
     * add caching
     * when user delete his account, deactivate his products
     * add email validation on registration using verfication code
     * add password reset functionality
     * */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
