package dci.j24e1.group1;

import java.sql.*;


public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/random_numbers_with_colors";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";
    static final String[] COLORS = {"yellow", "red", "blue", "green"};
    static final int RECORD_COUNT = 10000;
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

         new InsertRandomData(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
