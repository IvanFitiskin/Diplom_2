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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@Epic("User")
@RunWith(Parameterized.class)
public class TryCreateUserWithWrongDataTest {

    private AuthClient authClient;

    private String name;
    private String password;
    private String email;

    public TryCreateUserWithWrongDataTest(String name, String password, String email) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][] {
                { "FitiskinIP", null, "test-fitiskin-data@yandex.ru" },
                { null, "1234", "test-fitiskin-data@yandex.ru" },
                { "FitiskinIP", "", null },
                { "", "1234", "test-fitiskin-data@yandex.ru" },
                { "FitiskinIP", "1234", "" },
                { "FitiskinIP", "", "test-fitiskin-data@yandex.ru" }
        };
    }

    @Before
    public void setUp() {
        authClient = new AuthClient();
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Обычный позитивный кейс создания нового пользователя")
    public void createUserWithWrongDataTest() {
        User user = new User(name, password, email);
        ValidatableResponse response = authClient.create(user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_FORBIDDEN, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertFalse("success field is not corrected", isSuccess);

        String message = response.extract().path("message");
        assertEquals(
                "message is not corrected",
                "Email, password and name are required fields",
                message);
    }

}
