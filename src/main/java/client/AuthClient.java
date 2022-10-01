package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.UserCredentials;
import models.User;

import static io.restassured.RestAssured.given;

public class AuthClient extends RestClient {

    private static final String AUTH_PATH = "/api/auth";

    @Step("Отправка запроса на создание нового пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(AUTH_PATH + "/register")
                .then();
    }

    @Step("Отправка запроса на авторизацию пользователя")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .post(AUTH_PATH + "/login")
                .then();
    }

    @Step("Отправка запроса на обновление пользователя")
    public ValidatableResponse update(String token, User user) {
        return given()
                .spec(getBaseSpecWithToken(token))
                .body(user)
                .patch(AUTH_PATH + "/user")
                .then();
    }

    @Step("Отправка запроса на обновление пользователя (без токена)")
    public ValidatableResponse update(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .patch(AUTH_PATH + "/user")
                .then();
    }

    @Step("Отправка запроса на логаут пользователя")
    public ValidatableResponse logout(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .post(AUTH_PATH + "/logout")
                .then();
    }

    @Step("Отправка запроса на удаление пользователя")
    public ValidatableResponse delete(String token, String name) {
        return given()
                .spec(getBaseSpecWithToken(token))
                .queryParam("name", name)
                .when()
                .delete(AUTH_PATH + "/user")
                .then();
    }

    public void deleteExistUser(UserCredentials userCredentials) {
        ValidatableResponse responseLogin = login(userCredentials);

        if (responseLogin.extract().statusCode() == 200) {
            String token = responseLogin.extract().path("accessToken");
            String name = responseLogin.extract().path("user.name");
            delete(token, name);
        }
    }

}
