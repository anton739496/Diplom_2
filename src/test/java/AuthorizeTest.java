import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthorizeTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Логин 200 с существующим пользователя")
    public void loginShouldReturnOkWhenUserExists() {
        //given
        var json = new File(LOGIN_VALID_USER_JSON);

        //when-then
        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .and()
                .log().body()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo("smoke@high.com"))
                .body("user.name", equalTo("chich_chong"));
    }

    @Test
    @DisplayName("Логин 401 с неверными логином и паролем")
    public void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() {
        //given
        File json = new File(LOGIN_INVALID_USER_JSON);

        //when-then
        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
