import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetOrderListTest {

    private final User user = User.getRandomUser();
    List<String> ingredients = new ArrayList<>();
    public String orderIngredients;
    String accessToken;
    Ingredients createOrder;

    public int randomNumber(){
        Random rand = new Random();
        int upperbound = 10;
        return rand.nextInt(upperbound);
    }

    @Before
    public void setUp() {
        ingredients = OrderClient.getIngredients().extract().path("data._id");
        orderIngredients = ingredients.get(randomNumber());
        accessToken = UserClient.createUser(user).extract().path("accessToken");
        createOrder = new Ingredients(orderIngredients);
    }

    @After
    public void tearDown(){
        UserClient.deleteUserByUser(user);
    }

    @Test
    @DisplayName("Get orders list when user is authorized")
    public void getUserOrdersListWithLogin(){
         var getListOrders = OrderClient.getUserOrders(accessToken);

         int actualStatusCode = getListOrders.extract().statusCode();
         boolean isResponseSuccess = getListOrders.extract().path("success");

         Assert.assertEquals("Order list is not received", actualStatusCode, SC_OK);
         Assert.assertTrue(isResponseSuccess);
    }

    @Test
    @DisplayName("Get orders list when user is unauthorized")
    public void getUserOrdersListWithoutLogin(){
        var getOrdersList = OrderClient.getUserOrders("");
        String authorisedErrorMessage = "You should be authorised";

        int actualStatusCode = getOrdersList.extract().statusCode();
        boolean isResponseSuccess = getOrdersList.extract().path("success");
        String responseMessage = getOrdersList.extract().path("message");

        Assert.assertEquals("Order list received without user authorization",actualStatusCode, SC_UNAUTHORIZED);
        Assert.assertFalse(isResponseSuccess);
        Assert.assertEquals("Message:" + authorisedErrorMessage + "is not displayed", responseMessage, authorisedErrorMessage);
    }
}
