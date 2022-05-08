import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderTest {

    private final User user = User.getRandomUser();
    List<String> ingredients = new ArrayList<>();


    public int randomNumber(){
        Random rand = new Random();
        int upperbound = 10;
        return rand.nextInt(upperbound);
    }

    @After
    public void tearDown(){
        UserClient.deleteUserByUser(user);
    }

    @Test
    @DisplayName("Create order with login and valid ingredients")
    public void createValidOrder(){
        String accessToken = UserClient.createUser(user).extract().path("accessToken");
        ingredients = OrderClient.getIngredients().extract().path("data._id");
        Ingredients orderIngredients = new Ingredients(ingredients.get(randomNumber()));
        var createOrder = OrderClient.createOrder(orderIngredients, accessToken);

        int actualStatusCode = createOrder.extract().statusCode();
        boolean isResponseSuccess = createOrder.extract().path("success");
        int orderNumber = createOrder.extract().path("order.number");

        Assert.assertEquals(actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
        Assert.assertNotNull(orderNumber);
    }

    @Test
    @DisplayName("Create order without login")
    public void createOrderWithoutLogin(){
        ingredients = OrderClient.getIngredients().extract().path("data._id");
        Ingredients orderIngredients = new Ingredients(ingredients.get(randomNumber()));
        var createOrder = OrderClient.createOrder(orderIngredients, "");

        int actualStatusCode = createOrder.extract().statusCode();
        boolean isResponseSuccess = createOrder.extract().path("success");
        int orderNumber = createOrder.extract().path("order.number");

        Assert.assertEquals(actualStatusCode, SC_OK);
        Assert.assertTrue(isResponseSuccess);
        Assert.assertNotNull(orderNumber);
    }

    @Test
    @DisplayName("Create order without ingredients")
    public void createOrderWithoutIngredients(){
        String accessToken = UserClient.createUser(user).extract().path("accessToken");
        Ingredients orderIngredients = new Ingredients("");
        var createOrder = OrderClient.createOrder(orderIngredients, accessToken);
        String ingredientsErrorMessage = "Ingredient ids must be provided";

        int actualStatusCode = createOrder.extract().statusCode();
        boolean isResponseSuccess = createOrder.extract().path("success");
        String responseMessage = createOrder.extract().path("message");

        Assert.assertEquals(actualStatusCode, SC_BAD_REQUEST);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals(responseMessage, ingredientsErrorMessage);
    }

    @Test
    @DisplayName("Create order with invalid ingredient")
    public void createOrderWithInvalidIngredients(){
        String accessToken = UserClient.createUser(user).extract().path("accessToken");
        Ingredients invalidIngredients = new Ingredients("space cat fur");
        var createOrder = OrderClient.createOrder(invalidIngredients, accessToken);

        int actualStatusCode = createOrder.extract().statusCode();

        Assert.assertEquals(actualStatusCode, SC_INTERNAL_SERVER_ERROR);
    }

}
