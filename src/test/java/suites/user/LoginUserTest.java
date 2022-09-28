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
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

@Epic("User")
public class LoginUserTest {

    private User user;
    private UserCredentials userCredentials;
    private AuthClient authClient;

    @Before
    public void setUp() {
        user = UserGeneration.getDefault();
        authClient = new AuthClient();
        userCredentials = UserCredentials.from(user);

        authClient.create(user);
    }

    @After
    public void tearDown() {
        authClient.deleteExistUser(userCredentials);
    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Обычный позитивный кейс авторизации пользователя")
    public void positiveLoginUserTest() {
        ValidatableResponse response = authClient.login(userCredentials);

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

}
