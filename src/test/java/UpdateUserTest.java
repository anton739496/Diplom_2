import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Изменение имени пользователя 200")
    public void updateUserNameShouldReturnOk() {
        //given
        File json = new File(UPDATE_USERNAME_JSON);

        //when-then
        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo("smoke@high.com"))
                .body("user.name", equalTo("stone_man"))
                .log().body();
    }

    @Test
    @DisplayName("Изменение пароля пользователя 200")
    public void updateUserPasswordShouldReturnOk() {
        //given
        File json = new File(UPDATE_EMAIL_JSON);

        //when-then
        given().contentType("application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo("sip@high.com"))
                .body("user.name", equalTo("chich_chong"))
                .log().body();
    }

    @Test
    @DisplayName("Изменение имени пользователя 401 без токена")
    public void updateUserNameShouldReturnUnathorizedWhenTokenIsEmpty() {
        //given
        File json = new File(UPDATE_USERNAME_JSON);

        //when-then
        given().contentType("application/json")
                .and()
                .body(json)
                .when()
                .patch("/api/auth/user")
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .log().body();
    }
}
