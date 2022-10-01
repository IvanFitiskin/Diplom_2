package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.Order;
import models.User;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {

    private static final String ORDER_PATH = "/api/orders";

    @Step("Отправка запроса на получение заказа")
    public ValidatableResponse get() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Отправка запроса на получение заказов конкретного пользователя")
    public ValidatableResponse get(String token) {
        return given()
                .spec(getBaseSpecWithToken(token))
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Отправка запроса на создание заказа")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Отправка запроса на создание заказа конкретного пользователя")
    public ValidatableResponse create(Order order, String token) {
        return given()
                .spec(getBaseSpecWithToken(token))
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }
}
