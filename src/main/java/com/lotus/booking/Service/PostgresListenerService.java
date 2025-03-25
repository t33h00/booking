package com.lotus.booking.Service;

import com.lotus.booking.Config.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

@Service
public class PostgresListenerService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WebSocketHandler webSocketHandler;

    @PostConstruct
    public void listenToPostgres() {
        new Thread(() -> {
            try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
                 Statement statement = connection.createStatement()) {

                // Cast to PGConnection to access PostgreSQL-specific methods
                PGConnection pgConnection = connection.unwrap(PGConnection.class);

                statement.execute("LISTEN new_data_channel");
                System.out.println("Listening to PostgreSQL NOTIFY...");

                while (true) {
                    // Wait for a notification
                    PGNotification[] notifications = pgConnection.getNotifications(86400); // 5 seconds timeout

                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            String message = notification.getParameter();
                            System.out.println("New Data Received: " + message);
                            try {
                                webSocketHandler.sendMessageToClients(message);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    // Keep the connection alive
                    statement.executeQuery("SELECT 1");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
