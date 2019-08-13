package com.divya.greetings.repository;


import com.divya.greetings.models.Greet;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GreetResultSetMapper implements ResultSetMapper<Greet> {

    @Override
    public Greet map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Greet message = new Greet(r.getString("message"));
        return message;
    }
}
