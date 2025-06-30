package praktikum.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static praktikum.Client.spec;
import static praktikum.EnvConfig.*;

public class OrderClient {
    @Step("Отправка запроса на получение списка ингредиентов")
    public List<String> getIngredients() {
        return spec()
                .get(INGREDIENTS_URL)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data._id");
    }


    @Step("Отправка запроса на создание заказа")
    public ValidatableResponse createOrder(Order order, String tokenAfterLogin) {
        return spec()
                .header("Authorization", tokenAfterLogin)
                .body(order)
                .then().log().all()
                .when()
                .post(ORDERS_URL)
                .then();
    }

    @Step("Отправка запроса на получение заказа")
    public ValidatableResponse getOrder(String tokenAfterLogin) {
        return spec()
                .header("Authorization", tokenAfterLogin)
                .when()
                .get(ORDERS_URL)
                .then();
    }
}
