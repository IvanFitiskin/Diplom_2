package suites.order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.Order;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.junit.Assert.*;

@Epic("Order")
public class TryCreateOrderWithInvalidDataTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с несуществующий id ингредиента")
    @Description("Попытка создания заказа с несуществующий id ингредиента")
    public void positiveCreateOrderWithoutTokenTest() {
        String[] ingredients = { "61c0c5a71d1f82001bd6d" };
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.create(order);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_INTERNAL_SERVER_ERROR, statusCode);
    }

}
