package suites.order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.Order;
import models.OrderGeneration;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.junit.Assert.*;

public class TryCreateOrderWithInvalidDataTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Обычный позитивный кейс создания заказа для авторизованного пользователя")
    public void positiveCreateOrderWithoutTokenTest() {
        // Подсовываем несуществующий id ингредиента
        String[] ingredients = { "61c0c5a71d1f82001bd6d" };
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.create(order);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_INTERNAL_SERVER_ERROR, statusCode);
    }

}
