package com.pki.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Sqlite {
    public void db() throws Exception {

        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            System.out.println("SQLite DB connected");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
