package praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.order.Order;
import praktikum.order.OrderClient;
import praktikum.user.Credentials;
import praktikum.user.User;
import praktikum.user.UserChecks;
import praktikum.user.UserClient;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrderTest {
    private User user;
    private String accessToken;
    private final UserClient client = new UserClient();
    private final UserChecks check = new UserChecks();
    private final OrderClient orderClient = new OrderClient();
    private List<String> ingredientIds;

    @Before //Регистрация нового пользователя
    public void userRegistration() {
        user = User.random();
        accessToken = check.created(client.createUser(user));
    }

    @Before //Получение списка ингредиентов
    public void ingredients() {
        ingredientIds = orderClient.getIngredients();
    }

    @After //Удаление созданных пользователей
    public void delete() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getOrdersForUnauthorizedUser() {
        // Получение заказов без авторизации
        ValidatableResponse getResponse = orderClient.getOrder("");
        getResponse.assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void testGetOrdersForAuthorizedUser() {
        //Авторизация пользователя
        var creds = Credentials.fromUser(user);
        ValidatableResponse responseLogin = client.login(creds);
        String tokenAfterLogin = check.login(responseLogin);
        // Создание заказа
        Order order = new Order(ingredientIds);
        ValidatableResponse response = orderClient.createOrder(order, tokenAfterLogin);
        response.assertThat()
                .statusCode(200)
                .body("success", equalTo(true));

        // Получение заказов
        ValidatableResponse getResponse = orderClient.getOrder(accessToken);
        getResponse.assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }
}