package ca.gbc.orderservice;

import ca.gbc.orderservice.client.InventoryClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
class OrderServiceApplicationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("password");

    @LocalServerPort
    private Integer port;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private InventoryClient inventoryClient;

    private MockMvc mockMvc;

    static {
        postgreSQLContainer.start();
    }

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createOrderInStockTest() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "orderNumber": "ORD123",
                "skuCode": "SKU123",
                "price": 99.99,
                "quantity": 2
            }
        """;

        // Mocking the InventoryClient to return true
        given(inventoryClient.isInStock("SKU123", 2)).willReturn(true);

        mockMvc.perform(post("/api/order")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order Placed Successfully"));
    }

    @Test
    void createOrderOutOfStockTest() throws Exception {
        String requestBody = """
            {
                "id": 1,
                "orderNumber": "ORD123",
                "skuCode": "SKU123",
                "price": 99.99,
                "quantity": 2
            }
        """;

        // Mocking the InventoryClient to return false
        given(inventoryClient.isInStock("SKU123", 2)).willReturn(false);

        mockMvc.perform(post("/api/order")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Product with skuCode SKU123 is not in stock"));
    }
}
