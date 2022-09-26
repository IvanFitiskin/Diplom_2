package suites.user;

import client.AuthClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import models.UserCredentials;
import models.UserGeneration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.*;

public class UpdateUserTest {

    private User user;
    private User secondUser;
    private UserCredentials userCredentials;
    private String token;
    private AuthClient authClient;

    @Before
    public void setUp() {
        user = UserGeneration.getDefault();

        authClient = new AuthClient();
        userCredentials = UserCredentials.from(user);

        authClient.deleteExistUser(userCredentials);
        ValidatableResponse response = authClient.create(user);
        token = response.extract().path("accessToken");

    }

    @After
    public void tearDown() {
        userCredentials = UserCredentials.from(user);
        authClient.deleteExistUser(userCredentials);

        if (secondUser != null) {
            userCredentials = UserCredentials.from(secondUser);
            authClient.deleteExistUser(userCredentials);
        }
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    @Description("Обычный позитивный кейс обновления данных пользователя")
    public void positiveUpdateUserTest() {
        String oldName = user.getName();
        user.setName("new-name-" + oldName);

        String oldEmail = user.getEmail();
        user.setEmail("new-" + oldEmail);

        ValidatableResponse response = authClient.update(token, user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_OK, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertTrue("success field is not corrected", isSuccess);

        String name = response.extract().path("user.name");
        String email = response.extract().path("user.email");
        assertEquals("name does not match", user.getName(), name);
        assertEquals("email does not match", user.getEmail(), email);
        assertNotEquals("old name and new name do not match", user.getName(), oldName);
        assertNotEquals("old email and new email do not match", user.getEmail(), oldEmail);
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    @Description("Попытка обновления данных пользователя без авторизационного токена")
    public void positiveUpdateUserWithoutTokenTest() {
        ValidatableResponse response = authClient.update(user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_UNAUTHORIZED, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertFalse("success field is not corrected", isSuccess);

        String message = response.extract().path("message");
        assertEquals("message is not corrected", "You should be authorised", message);
    }

    @Test
    @DisplayName("Обновление данных пользователя")
    @Description("Попытка обновления данных пользователя с чужой почтой")
    public void positiveUpdateUserWithExistsEmailTest() {
        String oldEmail = user.getEmail();
        String newEmail = generateNewEmail("new", oldEmail);

        secondUser = UserGeneration.getDefault();
        secondUser.setEmail(newEmail);
        authClient.create(secondUser);

        user.setEmail(newEmail);

        ValidatableResponse response = authClient.update(token, user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_FORBIDDEN, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertFalse("success field is not corrected", isSuccess);

        String message = response.extract().path("message");
        assertEquals("message is not corrected", "User with such email already exists", message);
    }

    private String generateNewEmail(String salt, String email) {
        return salt + "-" + email;
    }

}
