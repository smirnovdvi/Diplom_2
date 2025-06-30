package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.user.Credentials;
import praktikum.user.User;
import praktikum.user.UserChecks;
import praktikum.user.UserClient;

import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserCredentialsTest {
    private String accessToken;
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();

    @Before //Регистрация нового пользователя
    public void userRegistration() {
        User user = User.random();
        accessToken = check.created(client.createUser(user));
    }

    @After //Удаление созданных пользователей
    public void delete() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка изменения информации о пользователе с авторизацией")
    public void updateUserDataWithLogin() {
        //Генерация новых учетных данных
        User newUserData = User.random();
        var newCreds = Credentials.fromUser(newUserData);
        //Изменение учетных данных
        ValidatableResponse response = client.update(accessToken, newUserData);
        response.assertThat().statusCode(200)
                .assertThat().body("user.email", equalTo(newCreds.getEmail()))
                .assertThat().body("user.name", equalTo(newCreds.getName()));
    }

    @Test
    @DisplayName("Проверка изменения информации о пользователе без авторизации")
    public void updateUserDataWithoutLogin() {
        User newUserData = User.random();
        ValidatableResponse response = client.update("wrongAccessToken", newUserData);
        response.assertThat()
                .statusCode(401)
                .body("message", Matchers.equalTo(check.message("You should be authorised")));

    }
}
