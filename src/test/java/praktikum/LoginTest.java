package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.user.Credentials;
import praktikum.user.User;
import praktikum.user.UserChecks;
import praktikum.user.UserClient;

import static org.hamcrest.Matchers.equalTo;

public class LoginTest {
    private User user;
    private String accessToken;
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();

    @Before //Регистрация нового пользователя
    public void userRegistration() {
        user = User.random();
        accessToken = check.created(client.createUser(user));
    }

    @After //Удаление созданных пользователей
    public void delete() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка успешной авторизации")
    public void userCanLogin() {
        var creds = Credentials.fromUser(user);
        ValidatableResponse response = client.login(creds);
        check.login(response);
    }

    @Test
    @DisplayName("Проверка авторизации с неверным логином и паролем")
    public void testLoginWithIncorrectParameters() {
        var incorrectCreds = new Credentials("wrongEmail", "wrongPassword", user.getName());
        ValidatableResponse response = client.login(incorrectCreds);
        response.assertThat()
                .statusCode(401)
                .body("message", equalTo(check.message("email or password are incorrect")));
    }
}

