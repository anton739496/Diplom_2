import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrdersTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Получение заказов 200")
    public void getOrdersShouldReturnOk() {
        //given
        createOrders(5);

        //when-then
        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .when()
                .get("/api/orders")
                .then()
                .log().body()
                .and()
                .assertThat()
                .statusCode(200)
                .and()
                .assertThat()
                .body("success", equalTo(true))
                .body("orders._id", hasSize(5))
                .body("orders.status", everyItem(equalTo("done")))
                .body("total", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов 401 с пустым токеном")
    public void getOrdersShouldReturnUnauthorizedWhenTokenIsEmpty() {
        //given
        createOrders(5);

        //when-then
        given().contentType("application/json")
                .when()
                .get("/api/orders")
                .then()
                .log().body()
                .and()
                .assertThat()
                .statusCode(401)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    private void createOrders(int amount) {
        var menu = getMenu();
        var random = new Random();
        for (int i = 0; i < amount; i++) {
            var buns = menu.getData().stream().filter(x -> x.getType().equals("bun")).toList();
            var mains = menu.getData().stream().filter(x -> x.getType().equals("main")).toList();
            var sauces = menu.getData().stream().filter(x -> x.getType().equals("sauce")).toList();
            var bun = buns.get(random.nextInt(buns.size()));
            var main = mains.get(random.nextInt(mains.size()));
            var sauce = sauces.get(random.nextInt(sauces.size()));
            var ingredients = Map.of("ingredients", List.of(bun, main, sauce));

            given().contentType("application/json")
                    .auth().oauth2(accessToken)
                    .body(ingredients)
                    .when()
                    .post("/api/orders")
                    .then()
                    .statusCode(200)
                    .and()
                    .body("success", equalTo(true))
                    .body("name", notNullValue())
                    .body("order.ingredients._id", hasItems(bun.getId(), main.getId(), sauce.getId()))
                    .body("order.owner.name", equalTo("chich_chong"))
                    .body("order.status", equalTo("done"))
                    .body("order.number", notNullValue());
        }
    }
}
