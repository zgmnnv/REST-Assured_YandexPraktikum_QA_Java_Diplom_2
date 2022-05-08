import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UserClient extends RestClient{


    public static final String REGISTER_PATH = "api/auth/register";
    public static final String LOGIN_PATH = "api/auth/login";
    public static final String USER_DATA_PATH = "api/auth/user";


    @Step("Create user")
    public static ValidatableResponse createUser(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("User login")
    public static ValidatableResponse login(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Change user data")
    public static ValidatableResponse changeUserData(String token, User user){
        return given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .when()
                .body(user)
                .patch(USER_DATA_PATH)
                .then();
    }

    @Step("Get access token")
    public static String getToken(User user){
        return login(user).extract().path("accessToken");
    }

    @Step("Delete user by token")
    public static void deleteUserByToken(String token){
        given()
                .header("Authorization", token)
                .spec(getBaseSpec()).when()
                .delete(USER_DATA_PATH)
                .then()
                .assertThat().statusCode(202);
    }

    @Step("Delete user by user name")
    public static void deleteUserByUser(User user) {
        if (getToken(user) != null) {
            deleteUserByToken(getToken(user));
        }
    }
}