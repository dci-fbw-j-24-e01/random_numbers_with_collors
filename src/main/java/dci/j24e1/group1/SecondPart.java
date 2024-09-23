package dci.j24e1.group1;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SecondPart {
    private static final String URL = "jdbc:postgresql://localhost:5432/random_numbers_with_colors";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);


        server.createContext("/", new MyHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server is listening on port 8080");
    }


    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String query = exchange.getRequestURI().getQuery();
            String numberParam = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair[0].equals("number") && pair.length > 1) {
                        numberParam = pair[1];
                        break;
                    }
                }
            }


            if (numberParam == null || numberParam.isEmpty()) {
                String response = "Please provide a 'number' parameter in the URL, e.g., ?number=500";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            int number;
            try {

                number = Integer.parseInt(numberParam);
            } catch (NumberFormatException e) {
                String response = "Invalid 'number' parameter. It should be an integer.";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }


            Map<String, Integer> colorCounts = fetchColorCountsByNumber(number);


            StringBuilder result = new StringBuilder();
            colorCounts.forEach((color, count) -> result.append(color).append(": ").append(count).append(", "));


            if (result.length() > 0) {
                result.setLength(result.length() - 2);
            } else {
                result.append("No results found for number: ").append(number);
            }


            String response = result.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }


        private Map<String, Integer> fetchColorCountsByNumber(int number) {
            Map<String, Integer> colorCounts = new HashMap<>();

            String sql = "SELECT color, COUNT(*) as count FROM random_numbers WHERE number = ? GROUP BY color";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {


                pstmt.setInt(1, number);


                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String color = rs.getString("color");
                        int count = rs.getInt("count");
                        colorCounts.put(color, count);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return colorCounts;
        }
    }
}
