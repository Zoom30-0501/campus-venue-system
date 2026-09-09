package com.club.venue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TableInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        // Only verify tables exist — actual DDL is in init.sql
        try {
            jdbc.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
            jdbc.queryForObject("SELECT COUNT(*) FROM tb_club", Integer.class);
            jdbc.queryForObject("SELECT COUNT(*) FROM tb_venue", Integer.class);
            System.out.println("All core tables verified.");
        } catch (Exception e) {
            System.out.println("Tables may not exist yet. Please run sql/init.sql first.");
        }
    }
}
