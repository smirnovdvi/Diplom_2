package praktikum.user;

import io.qameta.allure.Step;
import lombok.Value;

import java.util.concurrent.ThreadLocalRandom;

@Value
public class User {
    String email;
    String password;
    String name;

    @Step("Генерация случайного пользователя")
    public static User random() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 100_000);
        return new User("jack" + suffix + "@yandex.ru", "P@ssw0rd123", "Sparrow" + suffix);
    }
}
