package dci.j24e1.group1;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashMap;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        // Create an HTTP server listening on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define a simple handler that returns a response based on the "number" parameter
        server.createContext("/", new MyHandler());

        // Start the server
        server.start();
        System.out.println("Server started on port 8080");
    }

    // Handler to manage incoming requests
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Parse query parameters
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            // Get the "number" parameter from the query string
            String numberParam = queryParams.get("number");
            String response;

            if (numberParam != null) {
                try {
                    int number = Integer.parseInt(numberParam);
                    response = "You entered the number: " + number;
                } catch (NumberFormatException e) {
                    response = "Invalid number format.";
                }
            } else {
                response = "Please provide a 'number' parameter.";
            }

            // Send response back to client
            exchange.sendResponseHeaders(200, response.length()); // HTTP 200 OK
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        // Utility method to parse query parameters
        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> queryParams = new HashMap<>();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length > 1) {
                        queryParams.put(keyValue[0], keyValue[1]);
                    }
                }
            }
            return queryParams;
        }
    }
}