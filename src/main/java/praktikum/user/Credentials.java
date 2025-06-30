package praktikum.user;

import io.qameta.allure.Step;
import lombok.Value;

@Value
public class Credentials {
    String email;
    String password;
    String name;

    @Step("Получение учетных данных из сгенерированного пользователя")
    public static Credentials fromUser(User user) {
        return new Credentials(user.getEmail(), user.getPassword(), user.getName());
    }
}