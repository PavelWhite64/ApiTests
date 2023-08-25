import data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spetification.Spetifications;
import success.SuccessLogin;
import success.SuccessReg;
import unSuccess.UnSuccessLogin;
import unSuccess.UnSuccessReg;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    public final static String URL = "https://reqres.in";

    @Test
    public void checkAvatarAndIdTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecOK200());
        List<UserData> users = given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        //users.forEach(x-> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));

        //Assertions.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x -> x.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successRegTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecOK200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());

        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unSuccessRegTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecError400());
        Register user = new Register("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .post("/api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assertions.assertEquals("Missing password", unSuccessReg.getError());
    }

    @Test
    public void sortedYearsTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecOK200());
        List<ColorsData> colors = given()
                .when()
                .get("/api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortYears = years.stream().sorted().collect(Collectors.toList());

        Assertions.assertEquals(sortYears, years);
    }

    @Test
    public void deleteUserTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecUnique(204));
        given()
                .when()
                .delete("/api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecOK200());
        UserTime user = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(user)
                .when()
                .put("/api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);

        String currentTime = Clock.systemUTC().instant().toString().replaceAll("(.{5})$", "");
        Assertions.assertEquals(currentTime.replaceAll("(.{9})$", ""), response.getUpdatedAt().replaceAll("(.{8})$", ""));
    }

    @Test
    public void successfulLoginTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecOK200());
        String token = "QpwL5tke4Pnpja7X4";
        Login user = new Login("eve.holt@reqres.in", "cityslicka");
        SuccessLogin successLogin = given()
                .body(user)
                .when()
                .post("/api/login")
                .then().log().all()
                .extract().as(SuccessLogin.class);

        Assertions.assertNotNull(successLogin.getToken());

        Assertions.assertEquals(token, successLogin.getToken());
    }

    @Test
    public void unSuccessLogin() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecError400());
        Login user = new Login("peter@klaven", "");
        UnSuccessLogin unSuccessLogin = given()
                .body(user)
                .when()
                .post("/api/login")
                .then().log().all()
                .extract().as(UnSuccessLogin.class);
        Assertions.assertEquals("Missing password", unSuccessLogin.getError());
    }

    @Test
    public void singleUserNotFoundTest() {
        Spetifications.installSpecification(Spetifications.requestSpec(URL), Spetifications.responseSpecUnique(404));
        List<UserData> users = given()
                .when()
                .get("/api/users/23")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
    }
}