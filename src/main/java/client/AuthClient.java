package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import models.UserCredentials;
import models.User;

import static io.restassured.RestAssured.given;

public class AuthClient extends RestClient {

    private static final String AUTH_PATH = "/api/auth";

    @Step("Отправка запроса на создание нового пользователя")
    public ValidatableResponse create(User userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                .post(AUTH_PATH + "/register")
                .then();
    }

    @Step("Отправка запроса на авторизацию нового пользователя")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .post(AUTH_PATH + "/login")
                .then();
    }

    public ValidatableResponse update(String token, UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpecWithToken(token))
                .body(userLoginCredentials)
                .patch(AUTH_PATH + "/user")
                .then();
    }

    public ValidatableResponse logout(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .post(AUTH_PATH + "/logout")
                .then();
    }

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
