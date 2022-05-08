import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateUserTest {

    private final User user = User.getRandomUser();
    private final String fieldInputErrorMessage = "Email, password and name are required fields";
    private final String sameUserErrorMessage = "User already exists";

    @After
    public void tearDown(){
        UserClient.deleteUserByUser(user);
    }

    @Test
    @DisplayName("Create user with valid credentials")
    public void createUserWithValidCredentials(){
        var createResponse =
                UserClient.createUser(new User(user.getEmail(), user.getPassword(), user.getName()));

        boolean isResponseSuccess = createResponse.extract().path("success");
        var actualStatusCode = createResponse.extract().statusCode();

        Assert.assertEquals("User is not created",actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
    }

    @Test
    @DisplayName("Create user with same credentials")
    public void createUserWithSameCredentials(){
        UserClient.createUser(new User("test@test.ru", user.getPassword(), user.getName()));
        var createResponse =
                UserClient.createUser(new User("test@test.ru", user.getPassword(), user.getName()));

        boolean isResponseSuccess = createResponse.extract().path("success");
        String responseMessage = createResponse.extract().path("message");
        var actualStatusCode = createResponse.extract().statusCode();

        Assert.assertEquals("User was created with empty password", actualStatusCode, SC_FORBIDDEN);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + sameUserErrorMessage + "is not displayed", responseMessage, sameUserErrorMessage);
    }

    @Test
    @DisplayName("Create user with empty email")
    public void createUserWithEmptyEmail(){
        var createResponse =
                UserClient.createUser(new User("", user.getPassword(), user.getName()));

        boolean isResponseSuccess = createResponse.extract().path("success");
        String responseMessage = createResponse.extract().path("message");
        var actualStatusCode = createResponse.extract().statusCode();

        Assert.assertEquals("User was created with empty email", actualStatusCode, SC_FORBIDDEN);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + fieldInputErrorMessage + "is not displayed", responseMessage, fieldInputErrorMessage);
    }

    @Test
    @DisplayName("Create user with empty password")
    public void createUserWithEmptyPassword(){
        var createResponse =
                UserClient.createUser(new User(user.getEmail(), "", user.getName()));

        boolean isResponseSuccess = createResponse.extract().path("success");
        String responseMessage = createResponse.extract().path("message");
        int actualStatusCode = createResponse.extract().statusCode();

        Assert.assertEquals("User was created with empty password", actualStatusCode, SC_FORBIDDEN);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + fieldInputErrorMessage + "is not displayed", responseMessage, fieldInputErrorMessage);

    }

    @Test
    @DisplayName("Create user with empty name")
    public void createUserWithEmptyName(){
        var createResponse =
                UserClient.createUser(new User(user.getEmail(), user.getPassword(),""));

        boolean isResponseSuccess = createResponse.extract().path("success");
        String responseMessage = createResponse.extract().path("message");
        int actualStatusCode = createResponse.extract().statusCode();

        Assert.assertEquals("User was created with empty name", actualStatusCode, SC_FORBIDDEN);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + fieldInputErrorMessage + "is not displayed", responseMessage, fieldInputErrorMessage);
    }
}
