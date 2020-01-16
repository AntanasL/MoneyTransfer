package lt.fintech.api.adapter.transfers;

import com.sun.net.httpserver.HttpExchange;
import lt.fintech.api.adapter.Handler;
import lt.fintech.api.adapter.ResponseCode;
import lt.fintech.api.adapter.StandardResponse;
import lt.fintech.api.domain.models.Transfer;
import lt.fintech.api.domain.services.AccountService;
import lt.fintech.api.domain.services.TransferService;
import lt.fintech.api.infrastructure.AccountRepositoryImplementation;
import lt.fintech.api.infrastructure.TransferRepositoryImplementation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransferHandler implements Handler{
    
    private final TransferService transferService = new TransferService(new TransferRepositoryImplementation());
    private final AccountService accountService = new AccountService(new AccountRepositoryImplementation());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] handleResponse = null;
        StandardResponse<Transfer> standardResponse = null;

        if (exchange.getRequestMethod().equals("POST")) {
            standardResponse = executeTransfer(exchange.getRequestBody());
        } else {
            standardResponse = new StandardResponse<>(null, ResponseCode.METHOD_NOT_ALLOWED, "Method is not allowed");
        }

        if (standardResponse.getResponseCode() == ResponseCode.OK) {
            exchange.sendResponseHeaders(standardResponse.getResponseCode().getCode(), 0);
            handleResponse = createJsonFromObject(standardResponse.getResponseObject());
        } else {
            exchange.sendResponseHeaders(standardResponse.getResponseCode().getCode(), -1);
            handleResponse = standardResponse.getErrorMessage().getBytes();
        }

        OutputStream os = exchange.getResponseBody();
        os.write(handleResponse);
        os.flush();
        os.close();
    }

    private StandardResponse<Transfer> executeTransfer(InputStream is) {
        Transfer transfer = translateJsonToObject(is, Transfer.class);

        return transferService.transfer(transfer, accountService);
    }
}