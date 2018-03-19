package kz.kegoc.bln;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(
    basePackageClasses = { App.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
@EnableScheduling
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
