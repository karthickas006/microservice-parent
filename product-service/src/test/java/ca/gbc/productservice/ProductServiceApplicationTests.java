package ca.gbc.productservice;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

// Tells Spring Boot to look for a main configuration class (@SpringBootApp)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    // This annotation is used in combination with TestContainers to automatically configure the connection
    // to the test MongoDBContainer
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @LocalServerPort
    private Integer port;

    //http://localhost:port/api/product
    @BeforeEach
    void setup(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port; // Use the random port assigned to the app

    }

    static {
        mongoDBContainer.start(); // Ensure MongoDB is started
    }

    // TEST ONE: Create Product Test using RestAssured
    @Test
    void createProductTest(){

        String requestBody = """
            {
                "name": "Apple iPhone",
                "description": "iPhone 16 Plus",
                "price": 899.99
            }
        """;

        // BDD -0 Behavioural Driven Development: Its follow the pattern of (GIVEN, WHEN, THEN)
        RestAssured.given()
                .contentType("application/json") // Set request type to JSON
                .body(requestBody)  // Send product JSON as body
                .when()
                .post("/api/product") // POST to /api/product endpoint
                .then()
                .log().all()
                .statusCode(201) // The status code is 201
                .body("id", Matchers.notNullValue())                 // Ensure the ID is not null
                .body("name", Matchers.equalTo("Apple iPhone"))      // Check that name matches
                .body("description", Matchers.equalTo("iPhone 16 Plus"))  // Validate description
                .body("price", Matchers. equalTo(899.99F));



    }

    // TEST TWO: Post
    @Test
    void getAllProductsTest(){

        String requestBody = """
            {
                "name": "Apple iPhone",
                "description": "iPhone 16 Plus",
                "price": 899.99
            }
        """;

        // BDD -0 Behavioural Driven Development: Its follow the pattern of (GIVEN, WHEN, THEN)
        RestAssured.given()
                .contentType("application/json") // Set request type to JSON
                .body(requestBody)  // Send product JSON as body
                .when()
                .post("/api/product") // POST to /api/product endpoint
                .then()
                .log().all()
                .statusCode(201) // The status code is 201
                .body("id", Matchers.notNullValue())                 // Ensure the ID is not null
                .body("name", Matchers.equalTo("Apple iPhone"))      // Check that name matches
                .body("description", Matchers.equalTo("iPhone 16 Plus"))  // Validate description
                .body("price", Matchers.equalTo(899.99F));


        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/api/product")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", Matchers.greaterThan(0))
                .body("[0].name", Matchers.equalTo("Apple iPhone"))
                .body("[0].description", Matchers.equalTo("iPhone 16 Plus"))
                .body("[0].price", Matchers.equalTo(899.99F));

    }

}


