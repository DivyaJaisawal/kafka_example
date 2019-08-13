package com.divya.greetings.flyway;

import org.flywaydb.core.Flyway;

public class Migration {

    public static void main(String[] args) {
        Flyway flyway = new Flyway();
        flyway.setDataSource("jdbc:postgresql://localhost:5432/greeting_test",
                "postgres", "postgres");
        flyway.setBaselineOnMigrate(true);
        flyway.migrate();
    }
}