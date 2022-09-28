package suites.user;

import client.AuthClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.User;
import models.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Epic("User")
@RunWith(Parameterized.class)
public class TryLoginUserWithWrongDataTest {

    private AuthClient authClient;
    private UserCredentials userCredentials;

    private final String email;
    private final String password;

    public TryLoginUserWithWrongDataTest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][] {
                { "test-fitiskin-data@yandex.ru", null },
                { null, "1234" },
                { "test-fitiskin-data@yandex.ru", ""},
                { "", "1234" }
        };
    }

    @Before
    public void setUp() {
        authClient = new AuthClient();
        userCredentials = new UserCredentials(email, password);

        User user = new User("FitiskinIP", "1234","test-fitiskin-data@yandex.ru");
        authClient.create(user);
    }

    @After
    public void tearDown() {
        authClient.deleteExistUser(userCredentials);
    }

    @Test
    @DisplayName("Негативный тест авторизации пользователя")
    @Description("Попытка авторизации пользователя с неполными/неправильными данными")
    public void createUserWithWrongDataTest() {
        ValidatableResponse response = authClient.login(userCredentials);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code is not corrected", SC_UNAUTHORIZED, statusCode);

        boolean isSuccess = response.extract().path("success");
        assertFalse("success field is not corrected", isSuccess);

        String message = response.extract().path("message");
        assertEquals("message is not corrected", "email or password are incorrect", message);
    }

}
