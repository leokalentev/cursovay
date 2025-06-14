package hexlet.code.component;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DotenvLoader {
    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.load();
            System.out.println(".env переменные загружены");
        } catch (DotenvException ex) {
            System.out.println("Не удалось загрузить .env: " + ex.getMessage());
        }
    }
}

