package com.divya.greetings.repository;

import com.divya.greetings.models.Greet;
import org.skife.jdbi.v2.DBI;

import java.util.List;

public class GreetRepository {
    private DBI dbi;
    private int queryTimeOutInSeconds;
    private static final String INSERT_INTO_GREET = "INSERT INTO greet (message) VALUES (:message)";
    private static final String SELECT_QUERY = "SELECT * FROM greet";

    public GreetRepository(DBI dbi, int queryTimeOutInSeconds) {
        this.dbi = dbi;
        this.queryTimeOutInSeconds = queryTimeOutInSeconds;
    }

    public void saveGreetings(Greet greetMessage) {
        dbi.withHandle(handle -> {
            handle.createStatement(INSERT_INTO_GREET)
                    .bind("message", greetMessage.getMessage())
                    .execute();
            return null;
        });
    }
    public List<Greet> find() {
        return dbi.withHandle(handle -> handle.createQuery(SELECT_QUERY)
                .map(new GreetResultSetMapper())
                .setQueryTimeout(queryTimeOutInSeconds)
                .list()
        );
    }
}
