package suites.order;

import client.AuthClient;
import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class CreateOrderUnauthorizedUserTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Обычный позитивный кейс создания заказа для авторизованного пользователя")
    public void positiveCreateOrderWithoutTokenTest() {
        Order order = OrderGeneration.getDefault();
        ValidatableResponse response = orderClient.create(order);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_OK, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertTrue("success field is not corrected", isSuccess);

        String name = response.extract().path("name");
        assertEquals(
                "name does not match",
                "Space экзо-плантаго флюоресцентный традиционный-галактический бургер",
                name);

        String id = response.extract().path("order._id");
        assertNull("order id must be empty", id);
    }

}
