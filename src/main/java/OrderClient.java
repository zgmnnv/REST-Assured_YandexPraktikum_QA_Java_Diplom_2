import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient{

    public static final String INGREDIENTS_PATH = "api/ingredients";
    public static final String ORDERS_PATH = "api/orders";

    @Step("Create order")
    public static ValidatableResponse createOrder(Ingredients ingredients, String token){
        return given()
                .headers("Authorization", token)
                .spec(getBaseSpec())
                .body(ingredients)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Get user orders info")
    public static ValidatableResponse getUserOrders(String token){
        return given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .get(ORDERS_PATH)
                .then();
    }

    @Step("Get ingredients info")
    public static ValidatableResponse getIngredients(){
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH)
                .then();
    }
}
