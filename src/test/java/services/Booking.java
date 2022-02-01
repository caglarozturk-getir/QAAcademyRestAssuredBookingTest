package services;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Booking {

    String token;
    String createBookingID;

    @BeforeTest
    public void createToken(){
        String postData="{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";

        Response response = given()
                .log().all()
                .header("Content-Type","application/json")
                .body(postData)
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
                .then().statusCode(200)
                .log().all().extract().response();
        token=response.jsonPath().getString("token");
    }


    @Test(priority = 1)
    public void PostCreateBooking(){
        String requestBody="{\n" +
                "    \"firstname\" : \"Jim\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        Response requestID = given()
                .contentType(ContentType.JSON)
                .header("Content-Type","Application/json")
                .body(requestBody)
                .when()
                .post("https://restful-booker.herokuapp.com/booking")
                .then().statusCode(200)
                .log().all().extract().response();

        createBookingID= requestID.jsonPath().getString("bookingid");
    }

    @Test(priority = 2)
    public void getBooking (){
        String baseUrl ="https://restful-booker.herokuapp.com/booking";

        RequestSpecification requestSpecification = RestAssured.given().log().all();

        Response response = requestSpecification
                                                .log().all()
                                                .when().get(baseUrl);
        attachment(requestSpecification,baseUrl,response);
        Assert.assertEquals(response.getStatusCode(),200);

        /*
        given()
                .log().all()
                .when()
                .get("https://restful-booker.herokuapp.com/booking")
                .then().statusCode(200)
                .log().all();

         */
    }

    @DataProvider(name = "dataProvider")
    public Object[][] dataProvider(){
        return new Object[][]{
                {createBookingID}
        };
    }

    @Test(priority=3,dataProvider="dataProvider")
    public void getDetailsBooking(String id){
        given()
                .log().all()
                .when()
                .get("https://restful-booker.herokuapp.com/booking/"+id)
                .then()
                .log().all();
    }

    @Test(priority = 4)
    public void updateBooking(){

        String updateRequestBody ="{\n" +
                "    \"firstname\" : \"Jamessss\",\n" +
                "    \"lastname\" : \"Brownnnn\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        given()
                .header("Content-Type","application/json")
                .header("Cookie","token="+token)
                .log().all()
                .body(updateRequestBody)
                .when()
                .put("https://restful-booker.herokuapp.com/booking/"+createBookingID)
                .then().statusCode(200)
                .log().all();
    }

    @Test(priority=5)
    public void partialUpdateBooking(){
        Map<String,Object> queryParams= new HashMap<>();
        queryParams.put("firstname","Jamesiii");
        queryParams.put("lastname","Brownsss");

        given()
                .log().all()
                .header("Content-Type","application/json")
                .header("Cookie","token="+token)
                .body(queryParams)
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/"+createBookingID)
                .then()
                .statusCode(200)
                .log().all();
    }

    //@AfterClass
    @Test(priority = 6)
    public void deleteBooking(){
        given()
                .log().all()
                .header("Content-Type","Application/json")
                .header("Cookie","token="+token)
                .when()
                .delete("https://restful-booker.herokuapp.com/booking/"+createBookingID)
                .then().statusCode(201)
                .log().all();
    }

    public String attachment(RequestSpecification httpRequest, String baseUrl, Response response){

        String html = "Url= " + baseUrl + "\n\n" +
                "request header=" +((RequestSpecificationImpl) httpRequest).getHeaders() + "\n\n" +
                "request body=" +((RequestSpecificationImpl) httpRequest).getBody() + "\n\n" +
                "response body=" + response.getBody().asString();

                Allure.addAttachment("request detail", html);
                return html;
    }


}
