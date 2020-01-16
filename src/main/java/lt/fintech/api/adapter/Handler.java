package lt.fintech.api.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;

public interface Handler {

    public void handle(HttpExchange exchange) throws IOException;

    default <T> T translateJsonToObject(InputStream is, Class<T> valueClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        T jsonObject = null;

        try {
            jsonObject = objectMapper.readValue(is, valueClass);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    default <T> byte[] createJsonFromObject(T jsonObject) {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] byteStream = null;

        try {
            byteStream = objectMapper.writeValueAsBytes(jsonObject);
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return byteStream;
    }

    default String parsePath(String uriString) {
        return uriString.substring(uriString.lastIndexOf('/') + 1);
    }
}
