package praktikum.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import praktikum.Client;

import static praktikum.EnvConfig.*;

public class UserClient extends Client {


    @Step("Отправляем зарос на создание пользователя")
    public ValidatableResponse createUser(User user) {
        return spec()
                .body(user)
                .when()
                .post(REGISTER_URL)
                .then();
    }

    @Step("Отправляем запрос на удаление пользователя")
    public static void deleteUser(String accessToken) {
        spec()
                .header("Authorization", accessToken)
                .when()
                .delete(USER_URL)
                .then().log().all()
                .assertThat()
                .statusCode(202);
    }

    @Step("Отправляем запрос на авторизацию пользователя")
    public ValidatableResponse login(Credentials creds) {
        return spec()
                .body(creds)
                .when()
                .post(LOGIN_URL)
                .then();
    }

    @Step("Отправляем запроса на обновление данных пользователя")
    public ValidatableResponse update(String accessToken, User newUser) {
        return spec()
                .header("Authorization", accessToken)
                .body(newUser)
                .when()
                .patch(USER_URL)
                .then();
    }
}