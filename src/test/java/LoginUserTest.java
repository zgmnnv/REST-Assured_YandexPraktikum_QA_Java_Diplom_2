import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginUserTest {

    private final User user = User.getRandomUser();
    private final String loginErrorMessage = "email or password are incorrect";

    @Before
    public void setUp() {
        UserClient.createUser(user);
    }

    @After
    public void tearDown(){
        UserClient.deleteUserByUser(user);
    }

    @Test
    @DisplayName("Login user with valid credentials")
    public void loginUserWithValidCredentials(){
        var loginResponse =
                UserClient.login(new User(user.getEmail(), user.getPassword(), user.getName()));

        boolean isResponseSuccess = loginResponse.extract().path("success");
        int actualStatusCode = loginResponse.extract().statusCode();

        Assert.assertEquals("User is not authorized", actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
    }

    @Test
    @DisplayName("Login user with invalid email")
    public void loginUserWithInvalidEmail(){
        var loginResponse =
                UserClient.login(new User("ThisIsInvalidEmail@yandex.ru", user.getPassword(), user.getName()));

        boolean isResponseSuccess = loginResponse.extract().path("success");
        String responseMessage = loginResponse.extract().path("message");
        int actualStatusCode = loginResponse.extract().statusCode();

        Assert.assertEquals("Error. User login with invalid email", actualStatusCode, SC_UNAUTHORIZED);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + loginErrorMessage + "is not displayed", responseMessage, loginErrorMessage);
    }

    @Test
    @DisplayName("Login user with empty email")
    public void loginUserWithEmptyEmail(){
        var loginResponse = UserClient.login(new User("", user.getPassword(), user.getName()));

        boolean isResponseSuccess = loginResponse.extract().path("success");
        String responseMessage = loginResponse.extract().path("message");
        int actualStatusCode = loginResponse.extract().statusCode();

        Assert.assertEquals("Error. User login with empty email", actualStatusCode, SC_UNAUTHORIZED);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + loginErrorMessage + "is not displayed", responseMessage, loginErrorMessage);
    }


    @Test
    @DisplayName("Login user with invalid password")
    public void loginUserWithInvalidPassword(){
        var loginResponse =
                UserClient.login(new User(user.getEmail(), "ThisIsInvalidPassword", user.getName()));

        boolean isResponseSuccess = loginResponse.extract().path("success");
        String responseMessage = loginResponse.extract().path("message");
        int actualStatusCode = loginResponse.extract().statusCode();

        Assert.assertEquals("Error. User login with invalid password", actualStatusCode, SC_UNAUTHORIZED);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + loginErrorMessage + "is not displayed", responseMessage, loginErrorMessage);
    }

    @Test
    @DisplayName("Login user with empty password")
    public void loginUserWithEmptyPassword(){
        var loginResponse = UserClient.login(new User(user.getEmail(), "", user.getName()));

        boolean isResponseSuccess = loginResponse.extract().path("success");
        String responseMessage = loginResponse.extract().path("message");
        int actualStatusCode = loginResponse.extract().statusCode();

        Assert.assertEquals("Error. User login with empty password", actualStatusCode, SC_UNAUTHORIZED);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + loginErrorMessage + "is not displayed", responseMessage, loginErrorMessage);
    }

}
