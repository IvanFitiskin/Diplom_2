package suites.order;

import client.AuthClient;
import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@Epic("Order")
public class CreateOrderAuthorizedUserTest {

    private UserCredentials userCredentials;
    private OrderClient orderClient;
    private AuthClient authClient;
    private String token;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        authClient = new AuthClient();

        User user = UserGeneration.getDefault();
        userCredentials = UserCredentials.from(user);

        ValidatableResponse response = authClient.create(user);
        token = response.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        authClient.deleteExistUser(userCredentials);
    }

    @Test
    @DisplayName("Создание заказа с токеном юзера")
    @Description("Обычный позитивный кейс создания заказа для авторизованного пользователя")
    public void positiveCreateOrderWithTokenTest() {
        Order order = OrderGeneration.getDefault();
        ValidatableResponse response = orderClient.create(order, token);

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
        assertNotNull("order id must be filled", id);
    }

    @Test
    @DisplayName("Создание заказа с токеном юзера без без ингредиентов")
    @Description("Попытка создания заказа без ингредиентов для авторизованного пользователя")
    public void tryCreateOrderWithInvalidDataWithTokenTest() {
        String[] ingredients = {};
        Order order = new Order(ingredients);
        ValidatableResponse response = orderClient.create(order, token);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_BAD_REQUEST, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertFalse("success field is not corrected", isSuccess);

        String message = response.extract().path("message");
        assertEquals("message is not corrected", "Ingredient ids must be provided", message);
    }

}
