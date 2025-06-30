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

public class OrderTest {
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
        System.out.println(ingredientIds);
    }

    @After //Удаление созданных пользователей
    public void delete() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
    }


    @Test
    @DisplayName("Проверка создания заказа авторизованным пользователем")
    public void createOrderWithLogin() {
        //Авторизация пользователя
        var creds = Credentials.fromUser(user);
        ValidatableResponse responseLogin = client.login(creds);
        String tokenAfterLogin = check.login(responseLogin);
        //Создание заказа
        Order order = new Order(ingredientIds);
        ValidatableResponse response = orderClient.createOrder(order, tokenAfterLogin);
        response.assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    //Тест падает т.к., должна быть ошибка авторизации согласно документации, но заказ создается с кодом 200, чего не должно быть
    @DisplayName("Проверка ошибки создания заказа не авторизованным пользователем")
    public void createOrderWithoutLogin() {
        Order order = new Order(ingredientIds);
        ValidatableResponse response = orderClient.createOrder(order, ""); //Токен пустой, т.к., не было авторизации
        response.assertThat()
                .statusCode(401)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверка ошибки создания заказа авторизованным пользователем, но без ингредиентов")
    public void failedCreateOrderWithoutIngredients() {
        //Авторизация пользователя
        var creds = Credentials.fromUser(user);
        ValidatableResponse responseLogin = client.login(creds);
        String tokenAfterLogin = check.login(responseLogin);
        //Создание заказа без ингредиентов
        Order order = new Order(null);
        ValidatableResponse response = orderClient.createOrder(order, tokenAfterLogin);
        response.assertThat()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Проверка ошибки создания заказа авторизованным пользователем, но с неверным хешем")
    public void failedCreateOrderWithIncorrectHash() {
        //Авторизация пользователя
        var creds = Credentials.fromUser(user);
        ValidatableResponse responseLogin = client.login(creds);
        String tokenAfterLogin = check.login(responseLogin);
        // Создание списка ингредиентов с некорректным хешем
        List<String> ingredientsWithInvalidHash = List.of("invalidHash", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72");
        // Создание заказов
        Order order = new Order(ingredientsWithInvalidHash);
        ValidatableResponse response = orderClient.createOrder(order, tokenAfterLogin);
        response.assertThat()
                .statusCode(500);
    }
}
