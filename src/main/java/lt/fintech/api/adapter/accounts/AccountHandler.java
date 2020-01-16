package lt.fintech.api.adapter.accounts;

import com.sun.net.httpserver.HttpExchange;
import lt.fintech.api.adapter.Handler;
import lt.fintech.api.adapter.StandardResponse;
import lt.fintech.api.adapter.ResponseCode;
import lt.fintech.api.domain.models.Account;
import lt.fintech.api.domain.services.AccountService;
import lt.fintech.api.infrastructure.AccountRepositoryImplementation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AccountHandler implements Handler {

    private final AccountService accountService = new AccountService(new AccountRepositoryImplementation());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] handleResponse = null;
        StandardResponse<Account> standardResponse = null;

        if (exchange.getRequestMethod().equals("POST")) {
            standardResponse = saveAccount(exchange.getRequestBody());
        } else if (exchange.getRequestMethod().equals("GET")) {
            String accountId = parsePath(exchange.getRequestURI().toString());
            standardResponse = getAccount(accountId);
        } else {
            standardResponse = new StandardResponse<>(null, ResponseCode.METHOD_NOT_ALLOWED, "Method not allowed");
        }

        if (standardResponse.getResponseCode() == ResponseCode.OK) {
            exchange.sendResponseHeaders(standardResponse.getResponseCode().getCode(), 0);
            handleResponse = createJsonFromObject(standardResponse.getResponseObject());
        } else {
            exchange.sendResponseHeaders(standardResponse.getResponseCode().getCode(), -1);
        }

        OutputStream os = exchange.getResponseBody();
        os.write(handleResponse);
        os.flush();
        os.close();
    }

    private StandardResponse<Account> saveAccount(InputStream is) {
        Account account = translateJsonToObject(is, Account.class);

        accountService.create(account);

        return new StandardResponse<>(account, ResponseCode.OK, "");
    }

    private StandardResponse<Account> getAccount(String accountId) {
        Account account = accountService.find(accountId);

        ResponseCode rc = (account != null) ? ResponseCode.OK : ResponseCode.BAD_REQUEST;

        return new StandardResponse<>(account, rc, "");
    }
}