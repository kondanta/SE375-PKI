package com.pki.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Sqlite {
    private static final String URL = "jdbc:sqlite:data.db";

    public void db(){
        createDb();
        createTable();
        insertPerson("Thomas", 15);
        insertPerson("Walter", 32);
    }

    private static void insertPerson(String name, int age) {
        final String SQL = "INSERT INTO persons VALUES(?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL);) {
            ps.setString(1, name); // First question mark will be replaced by name variable - String;
            ps.setInt(2, age); // Second question mark will be replaced by name variable - Integer;
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable() {
        final String SQL = "CREATE TABLE IF NOT EXISTS persons (name TEXT, age INTEGER);";
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
        return DriverManager.getConnection(URL);
    }
}

