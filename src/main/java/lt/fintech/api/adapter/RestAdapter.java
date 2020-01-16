package lt.fintech.api.adapter;

import com.sun.net.httpserver.HttpServer;
import lt.fintech.api.adapter.accounts.AccountHandler;
import lt.fintech.api.adapter.transfers.TransferHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestAdapter {

    public static void main(String[] args) throws IOException {
        int serverPort = 8888;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        AccountHandler accountHandler = new AccountHandler();
        TransferHandler transferHandler = new TransferHandler();

        server.createContext("/account", accountHandler::handle);
        server.createContext("/transfer", transferHandler::handle);
        server.setExecutor(null);
        server.start();
    }
}
