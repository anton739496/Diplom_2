import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {

    @After
    public void tearDown() {
        deleteUser();
    }

    @Before
    public void setUp() {
        accessToken = getToken();
    }

    @Test
    @DisplayName("Создание заказа 200 без токена")
    public void createOrderShouldReturnOkWhenTokenIsEmpty() {
        //given
        var menu = getMenu();
        var bun = menu.getData().stream().filter(x -> x.getType().equals("bun")).findFirst().get();
        var main = menu.getData().stream().filter(x -> x.getType().equals("main")).findFirst().get();
        var ingredients = Map.of("ingredients", List.of(bun, main));

        //when-then
        given().contentType("application/json")
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа 200 с токеном, с ингредиентами")
    public void createOrderShouldReturnOkWhenTokenPassed() {
        //given
        var menu = getMenu();
        var bun = menu.getData().stream().filter(x -> x.getType().equals("bun")).findFirst().get();
        var main = menu.getData().stream().filter(x -> x.getType().equals("main")).findFirst().get();
        var ingredients = Map.of("ingredients", List.of(bun.getId(), main.getId()));

        //when-then
        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.ingredients._id", hasItems(bun.getId(), main.getId()))
                .body("order.owner.name", equalTo("chich_chong"))
                .body("order.status", equalTo("done"))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа 400 без ингредиентов")
    public void createOrderShouldReturnBadRequestWhenNoIngredientsPassed() {
        //when-then
        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .and()
                .assertThat()
                .statusCode(400)
                .and()
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа 500 с неверным хэшем")
    public void createOrderShouldReturnInternalServerErrorWhenInvalidHash() {
        //given
        var ingredients = Map.of("ingredients", List.of("11dfac0c5a71d1f82001bdaaa6f"));

        //when-then
        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .body(ingredients)
                .when()
                .post("/api/orders")
                .then()
                .log().body()
                .and()
                .assertThat()
                .statusCode(500);
    }
}
