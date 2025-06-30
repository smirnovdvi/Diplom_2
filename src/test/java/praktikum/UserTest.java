package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import praktikum.user.UserChecks;
import praktikum.user.UserClient;
import praktikum.user.User;

import static org.hamcrest.Matchers.equalTo;

public class UserTest {
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
    private String accessToken;

    @After //Удаление созданных пользователей
    public void delete() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Проверка успешного создания пользователя")
    public void testCreateUser() {
        var user = User.random();
        ValidatableResponse createResponse = client.createUser(user);
        accessToken = check.created(createResponse);
    }

    @Test
    @DisplayName("Проверка невозможности регистрации двух одинаковых пользователей")
    public void duplicateUserCreationFailed() {
        var user = User.random();
        ValidatableResponse createResponse = client.createUser(user);
        check.created(createResponse);
        ValidatableResponse duplicateResponse = client.createUser(user);
        duplicateResponse.assertThat()
                .statusCode(403)
                .body("message", equalTo(check.message("User already exists")));// Проверка ошибки дублирования
    }

    @Test
    @DisplayName("Проверка невозможности создания пользователя без обязательного поля")
    public void missingFieldsInUserCreationFailed() {
        var user = new User(null, "password", "name"); // Пропущенное обязательное поле
        ValidatableResponse createResponse = client.createUser(user);
        createResponse.assertThat()
                .statusCode(403)
                .body("message", equalTo(check.message("Email, password and name are required fields"))); // Проверка отсутствия обязательного поля
    }
}