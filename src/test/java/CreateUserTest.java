import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest extends BaseTest {

    @Before
    public void setUp() {
        accessToken = getToken();
        deleteUser();
    }

    @After
    public void tearDown() {
        deleteUser();
    }

    @Test
    @DisplayName("Создание уникального пользователя.")
    public void createUserShouldReturnOk() {
        //given
        File json = new File(CREATE_NEW_USER_JSON);

        //when-then
        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя 403 при созданном пользователе")
    public void createUserShouldReturnForbiddenWhenUserAlreadyExists() {
        //given
        accessToken = createUser();

        File json = new File(CREATE_NEW_USER_JSON);

        //when-then
        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя 403 при некорректном json")
    public void createUserShouldReturnForbiddenWhenJsonIsInvalid() {
        //given
        File json = new File(CREATE_INVALID_USER_JSON);

        //when-then
        given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    private String createUser() {
        File json = new File(CREATE_NEW_USER_JSON);

        String token = given().contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .extract()
                .path("accessToken");

        return token.substring(7);
    }
}
