package suites.user;

import client.AuthClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import models.UserCredentials;
import models.UserGeneration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@Epic("User")
public class CreateUserTest {

    private User user;
    private UserCredentials userCredentials;
    private AuthClient authClient;

    @Before
    public void setUp() {
        user = UserGeneration.getDefault();
        authClient = new AuthClient();
        userCredentials = UserCredentials.from(user);

        authClient.deleteExistUser(userCredentials);
    }

    @After
    public void tearDown() {
        authClient.deleteExistUser(userCredentials);
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Обычный позитивный кейс создания нового пользователя")
    public void positiveCreateUserTest() {
        ValidatableResponse response = authClient.create(user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_OK, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertTrue("success field is not corrected", isSuccess);

        String name = response.extract().path("user.name");
        String email = response.extract().path("user.email");
        assertEquals("name does not match", user.getName(), name);
        assertEquals("email does not match", user.getEmail(), email);

        String accessToken = response.extract().path("accessToken");
        String refreshToken = response.extract().path("refreshToken");
        assertNotNull("accessToken must not be null", accessToken);
        assertNotNull("refreshToken must not be null", refreshToken);
        assertFalse("accessToken must not be empty", accessToken.isEmpty());
        assertFalse("refreshToken must not be empty", refreshToken.isEmpty());
    }

    @Test
    @DisplayName("Создание пользователя с уже использованными данными")
    @Description("Попытка создания нового пользователя с уже использованными данными")
    public void tryCreateUserTwiceTest() {
        ValidatableResponse response = authClient.create(user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_OK, statusCode);

        ValidatableResponse secondResponse = authClient.create(user);

        int newStatusCode = secondResponse.extract().statusCode();
        assertEquals("Status code is not corrected", SC_FORBIDDEN, newStatusCode);

        boolean isSuccess = secondResponse.extract().path("success");
        assertFalse("success field is not corrected", isSuccess);

        String message = secondResponse.extract().path("message");
        assertEquals("message is not corrected", "User already exists", message);
    }
}
