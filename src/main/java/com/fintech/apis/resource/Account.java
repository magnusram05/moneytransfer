package com.fintech.apis.resource;

import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.exception.UserInputException;
import com.fintech.apis.factory.RequestHandlerFactory;
import com.fintech.apis.handler.IHandler;
import com.fintech.apis.model.BaseResponse;
import com.fintech.apis.model.MoneyTransferRequest;
import com.fintech.apis.model.UserAccountRequest;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("account/v1")
public class Account {
    private final IHandler<UserAccountRequest, com.fintech.apis.model.Account> userAccountHandler = RequestHandlerFactory.getUserAccountHandler();
    private final IHandler<MoneyTransferRequest, BaseResponse> moneyTransferHandler = RequestHandlerFactory.getMoneyTransferHandler();

    private final Gson gson = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("balance")
    public Response getBalance(String userAccountRequest) {
        try {
            com.fintech.apis.model.Account account = this.userAccountHandler.handle(gson.fromJson(userAccountRequest, UserAccountRequest.class));
            BaseResponse successResponse = BaseResponse.successResponse(Arrays.asList("Account Balance: " + account.getCurrentBalance()));
            return Response
                    .status(Response.Status.OK)
                    .entity(successResponse)
                    .build();
        } catch (UserInputException | DataValidationException ex) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(BaseResponse.errorResponse(Arrays.asList(ex.getMessage())))
                    .build();
        } catch (Exception ex) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(BaseResponse.errorResponse(Arrays.asList(ex.getMessage())))
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("moneytransfer")
    public Response transferMoney(String moneyTransferRequest) {
        try {
            BaseResponse response = this.moneyTransferHandler.handle(gson.fromJson(moneyTransferRequest, MoneyTransferRequest.class));

            return Response
                    .status(Response.Status.OK)
                    .entity(response)
                    .build();
        } catch (UserInputException | DataValidationException ex) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(BaseResponse.errorResponse(Arrays.asList(ex.getMessage())))
                    .build();
        } catch (Exception ex) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(BaseResponse.errorResponse(Arrays.asList(ex.getMessage())))
                    .build();
        }
    }

    @GET
    @Path("health")
    public Response healthCheck() {
        return Response
                .status(Response.Status.OK)
                .entity(BaseResponse.ResponseCode.SUCCESS.name())
                .build();
    }
}
