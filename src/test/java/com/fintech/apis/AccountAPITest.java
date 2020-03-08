package com.fintech.apis;

import com.fintech.apis.factory.RequestHandlerFactory;
import com.fintech.apis.handler.IHandler;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.BaseResponse;
import com.fintech.apis.model.MoneyTransferRequest;
import com.fintech.apis.model.UserAccountRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.IntStream;

import static com.fintech.apis.util.AppConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountAPITest {
    private static final Logger LOGGER = LogManager.getLogger(AccountAPITest.class);

    private HttpServer server;
    private WebTarget target;
    private IHandler<UserAccountRequest, Account> userAccountHandler;
    private IHandler<MoneyTransferRequest, BaseResponse> moneyTransferHandler;

    @BeforeEach
    public void setUp() throws Exception {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
        userAccountHandler = RequestHandlerFactory.getUserAccountHandler();
        moneyTransferHandler = RequestHandlerFactory.getMoneyTransferHandler();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
    }


    @Test
    @Order(1)
    public void testMoneyTransfer_Happy() throws Exception {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(1)
                .setToAccount(2)
                .setAmountToTransfer("1")
                .build();
        try {
            moneyTransferHandler.handle(moneyTransferRequest);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        assertAccountBalanceIsValid();
    }

    @Test
    @Order(2)
    public void testMoneyTransfer_concurrency() throws Exception {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(1)
                .setToAccount(2)
                .setAmountToTransfer("998")
                .build();
        Thread thread1 = null;
        Thread thread2 = null;
        for (int i = 0; i < 1000; i++) {
            thread1 = new Thread(() -> {
                try {
                    moneyTransferHandler.handle(moneyTransferRequest);
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            });
            thread2 = new Thread(() -> {
                try {
                    moneyTransferHandler.handle(moneyTransferRequest);
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            });
            thread1.start();
            thread2.start();
        }

        thread1.join();
        thread2.join();

        UserAccountRequest account1 = new UserAccountRequest(1, 1);
        UserAccountRequest account2 = new UserAccountRequest(2, 2);

        IntStream.range(0, 1000).forEach(i -> {
            try {
                Account account1Balance = userAccountHandler.handle(account1);
                assertEquals(1, account1Balance.getCurrentBalance().doubleValue(), 0);
                Account account2Balance = userAccountHandler.handle(account2);
                assertEquals(2999, account2Balance.getCurrentBalance().doubleValue(), 0);
            } catch (Exception ex) {
                fail();
            }
        });
    }

    private void assertAccountBalanceIsValid() {
        UserAccountRequest account1 = new UserAccountRequest(1, 1);
        UserAccountRequest account2 = new UserAccountRequest(2, 2);

        IntStream.range(0, 1000).forEach(i -> {
            try {
                Account account1Balance = userAccountHandler.handle(account1);
                assertTrue(account1Balance.getCurrentBalance().doubleValue() >= 0);
                Account account2Balance = userAccountHandler.handle(account2);
                assertTrue(account2Balance.getCurrentBalance().doubleValue() >= 0);

                assertTrue(account2Balance.getCurrentBalance().compareTo(account1Balance.getCurrentBalance()) > 0);
            } catch (Exception ex) {
                fail();
            }
        });
    }

    @Test
    @Order(3)
    public void testMoneyTransfer_happy() {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(1)
                .setToAccount(2)
                .setAmountToTransfer("1")
                .build();
        Response response = target.path("account/v1/moneytransfer").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(moneyTransferRequest, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertBalanceCheckSuccessWithMessage(new UserAccountRequest(1, 1), "Account Balance: 0.0");
        assertBalanceCheckSuccessWithMessage(new UserAccountRequest(2, 2), "Account Balance: 3000.0");
    }

    @Test
    @Order(4)
    public void testMoneyTransfer_invalidUser() {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(-1)
                .setFromAccount(1)
                .setToAccount(2)
                .setAmountToTransfer("1")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest, Response.Status.FORBIDDEN, INVALID_USER);
    }

    @Test
    @Order(5)
    public void testMoneyTranfer_invalidFromAccount() {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(-1)
                .setToAccount(2)
                .setAmountToTransfer("1")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest, Response.Status.FORBIDDEN, INVALID_DEBIT_ACCOUNT);
    }

    @Test
    @Order(6)
    public void testMoneyTranfer_invalidToAccount() {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(1)
                .setToAccount(-2)
                .setAmountToTransfer("1")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest, Response.Status.FORBIDDEN, INVALID_CREDIT_ACCOUNT);
    }

    @Test
    @Order(7)
    public void testMoneyTranfer_invalidAmount() {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(1)
                .setToAccount(2)
                .setAmountToTransfer("-1")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest, Response.Status.FORBIDDEN, INVALID_AMOUNT);
    }

    @Test
    @Order(8)
    public void testMoneyTranfer_accountDoesntMatchUser() {
        MoneyTransferRequest moneyTransferRequest1 = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(2)
                .setToAccount(3)
                .setAmountToTransfer("1")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest1, Response.Status.FORBIDDEN, INVALID_DEBIT_ACCOUNT);

        MoneyTransferRequest moneyTransferRequest2 = MoneyTransferRequest.newBuilder()
                .setUserID(2)
                .setFromAccount(1)
                .setToAccount(3)
                .setAmountToTransfer("1")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest2, Response.Status.FORBIDDEN, INVALID_DEBIT_ACCOUNT);
    }

    @Test
    @Order(9)
    public void testMoneyTranfer_insufficientAccountBalance() {
        MoneyTransferRequest moneyTransferRequest = MoneyTransferRequest.newBuilder()
                .setUserID(1)
                .setFromAccount(1)
                .setToAccount(2)
                .setAmountToTransfer("10000000")
                .build();
        assertMoneyTransferErrorWithMessage(moneyTransferRequest, Response.Status.FORBIDDEN, INSUFFICIENT_BALANCE);
    }

    private void assertMoneyTransferErrorWithMessage(MoneyTransferRequest moneyTransferRequest, Response.Status status, String message) {
        Response response = target.path("account/v1/moneytransfer").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(moneyTransferRequest, MediaType.APPLICATION_JSON));

        assertEquals(status.getStatusCode(), response.getStatus());

        BaseResponse.Builder baseResponseBuilder = response.readEntity(BaseResponse.Builder.class);
        BaseResponse baseResponse = baseResponseBuilder.build();
        assertNotNull(baseResponse.getErrorMessages());
        assertTrue(baseResponse.getErrorMessages().get(0).contains(message));
    }

    private void assertBalanceCheckSuccessWithMessage(UserAccountRequest userAccountRequest, String message) {
        Response response = target.path("account/v1/balance").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(userAccountRequest, MediaType.APPLICATION_JSON));

        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        BaseResponse.Builder baseResponseBuilder = response.readEntity(BaseResponse.Builder.class);
        BaseResponse baseResponse = baseResponseBuilder.build();
        assertNotNull(baseResponse.getSuccessMsgs());
        assertTrue(baseResponse.getSuccessMsgs().contains(message));
    }

}
