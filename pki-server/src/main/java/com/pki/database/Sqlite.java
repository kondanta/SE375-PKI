package com.pki.database;


import java.security.PrivateKey;
import java.sql.*;

public class Sqlite {
    private static final String URL = "jdbc:sqlite:data.db";

    public Sqlite(){
        createDb();
        createTable();
    }

    public static void insertPerson(String name, String email, PrivateKey key) {
        final String SQL = "INSERT INTO persons VALUES(?,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL);) {
            ps.setString(1, name); // First question mark will be replaced by name variable - String;
            ps.setString(2, email); // Second question mark will be replaced by name variable - Integer;
            ps.setObject(3, key);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        final String SQL = "CREATE TABLE IF NOT EXISTS persons (name TEXT, email TEXT, publicKey TEXT);";
        // This SQL Query is not "dynamic". Columns are static, so no need to use
        // PreparedStatement.
        try (Connection con = getConnection(); Statement statement = con.createStatement();) {
            statement.executeUpdate(SQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDb() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                conn.getMetaData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
        }
        return null;
    }
}

