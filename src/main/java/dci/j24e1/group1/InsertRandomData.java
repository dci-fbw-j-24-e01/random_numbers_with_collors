package dci.j24e1.group1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static dci.j24e1.group1.Main.COLORS;
import static dci.j24e1.group1.Main.RECORD_COUNT;

public class InsertRandomData {


    InsertRandomData(Connection conn) throws SQLException {

        String sql = "INSERT INTO random_numbers (number, color) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            Random random = new Random();

            for (int i = 0; i < RECORD_COUNT; i++) {
                int number = random.nextInt(1000) + 1;
                String color = COLORS[random.nextInt(COLORS.length)];


                preparedStatement.setInt(1, number);
                preparedStatement.setString(2, color);

                preparedStatement.addBatch();


                if (i % 1000 == 0) {
                    preparedStatement.executeBatch();
                }
            }

            preparedStatement.executeBatch();
            System.out.println(RECORD_COUNT + " entries have been inserted");
        }
    }
}
