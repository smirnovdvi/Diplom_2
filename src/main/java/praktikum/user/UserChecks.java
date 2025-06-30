package praktikum.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static org.junit.Assert.assertNotEquals;

public class UserChecks {

    @Step("Проверяем корректность создания нового пользователя")
    public String created(ValidatableResponse response) {
        String accessToken = response
                .assertThat()
                .statusCode(200)
                .extract()
                .path("accessToken");
        assertNotEquals(null, accessToken);
        return accessToken;
    }

    @Step("Проверяем корректность авторизации пользователя")
    public String login(ValidatableResponse response) {
        String accessToken = response
                .assertThat()
                .statusCode(200)
                .extract()
                .path("accessToken");
        assertNotEquals(null, accessToken);
        return accessToken;
    }


    @Step("Формирование сообщения об ошибке")
    public String message(String expectedMessage) {
        return expectedMessage;
    }

}
