import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChangeUserTest {

    private final User user = User.getRandomUser();

    @Before
    public void setUp() {
        UserClient.createUser(user);
    }

    @After
    public void tearDown(){
        UserClient.deleteUserByUser(user);
    }

    @Test
    @DisplayName("Change user email with authorization")
    public void changeUserEmail(){
        String userEmail = User.getRandomEmail();
        User user = new User(userEmail, this.user.getPassword(), this.user.getName());
        var changeResponse = UserClient.changeUserData(UserClient.getToken(this.user), user);
        
        boolean isResponseSuccess = changeResponse.extract().path("success");
        String changedEmail = changeResponse.extract().path("user.email");
        int actualStatusCode = changeResponse.extract().statusCode();

        Assert.assertEquals(actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
        Assert.assertEquals(changedEmail,userEmail.toLowerCase());
    }

    @Test
    @DisplayName("Change user password with authorization")
    public void changeUserPassword(){
        String newPassword = User.getRandomData();
        User newUser = new User(user.getEmail(), newPassword, user.getName());
        var changeResponse = UserClient.changeUserData(UserClient.getToken(user), newUser);
        
        boolean isResponseSuccess = changeResponse.extract().path("success");
        int actualStatusCode = changeResponse.extract().statusCode();

        Assert.assertEquals(actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
    }

    @Test
    @DisplayName("Change user name with authorization")
    public void changeUserName(){
        String newUserName = User.getRandomData();
        User changedUser = new User(user.getEmail(), user.getPassword(), newUserName);
        var changeResponse = UserClient.changeUserData(UserClient.getToken(user), changedUser);
        
        boolean isResponseSuccess = changeResponse.extract().path("success");
        String actualUserName = changeResponse.extract().path("user.name");
        int actualStatusCode = changeResponse.extract().statusCode();


        Assert.assertEquals(actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
        Assert.assertEquals(actualUserName, newUserName);
    }

    @Test
    @DisplayName("Change user data without authorization")
    public void changeUserDataWithoutLogin(){
        var changeResponse = UserClient.changeUserData("", user);
        String authorisedErrorMessage = "You should be authorised";
        
        boolean isResponseSuccess = changeResponse.extract().path("success");
        String responseMessage = changeResponse.extract().path("message");
        int actualStatusCode = changeResponse.extract().statusCode();

        Assert.assertEquals(actualStatusCode, SC_UNAUTHORIZED);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + authorisedErrorMessage + "is not displayed", responseMessage, authorisedErrorMessage);

    }

    @Test
    @DisplayName("Set the used email address")
    public void changeUserEmailToUsedEmail(){
        User firstUser = User.getRandomUser();
        UserClient.createUser(firstUser);
        String firstUserEmail = firstUser.getEmail();
        User secondUser = new User (firstUserEmail, user.getPassword(), user.getName());

        var changeResponse = UserClient.changeUserData(UserClient.getToken(user), secondUser);
        String existsEmailErrorMessage = "User with such email already exists";

        boolean isResponseSuccess = changeResponse.extract().path("success");
        String responseMessage = changeResponse.extract().path("message");
        int actualStatusCode = changeResponse.extract().statusCode();

        Assert.assertEquals(actualStatusCode, SC_FORBIDDEN);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + existsEmailErrorMessage + "is not displayed", responseMessage, existsEmailErrorMessage);
    }
}
