package airline.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:airline.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        full_name TEXT NOT NULL,
                        username TEXT NOT NULL UNIQUE,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL
                    )
                    """);

                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS flights (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        flight_number TEXT NOT NULL UNIQUE,
                        airline_name TEXT NOT NULL,
                        source TEXT NOT NULL,
                        destination TEXT NOT NULL,
                        departure_time TEXT NOT NULL,
                        arrival_time TEXT NOT NULL,
                        price REAL NOT NULL,
                        seats_available INTEGER NOT NULL
                    )
                    """);

                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS routes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        flight_number TEXT NOT NULL,
                        stop_order INTEGER NOT NULL,
                        city TEXT NOT NULL
                    )
                    """);

                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS bookings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        passenger_name TEXT NOT NULL,
                        passenger_email TEXT NOT NULL,
                        flight_number TEXT NOT NULL,
                        source TEXT NOT NULL,
                        destination TEXT NOT NULL,
                        booking_date TEXT NOT NULL,
                        seat_count INTEGER NOT NULL,
                        total_fare REAL NOT NULL,
                        status TEXT NOT NULL
                    )
                    """);
            }

            seedUsers();
            seedFlights();
            seedRoutes();
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("SQLite JDBC driver not found. Add sqlite-jdbc JAR to lib/ and classpath.", ex);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to initialize database.", ex);
        }
    }

    private static void seedUsers() throws SQLException {
        if (tableHasRows("users")) {
            return;
        }

        String sql = "INSERT INTO users (full_name, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            insertUser(ps, "Project Administrator", "admin", "admin123", "ADMIN");
            insertUser(ps, "Booking Officer", "staff", "staff123", "STAFF");
        }
    }

    private static void insertUser(
        PreparedStatement ps,
        String fullName,
        String username,
        String password,
        String role
    ) throws SQLException {
        ps.setString(1, fullName);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setString(4, role);
        ps.executeUpdate();
    }

    private static void seedFlights() throws SQLException {
        if (tableHasRows("flights")) {
            return;
        }

        String sql = """
            INSERT INTO flights
            (flight_number, airline_name, source, destination, departure_time, arrival_time, price, seats_available)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            insertFlight(ps, "AI-203", "SkyBridge Air", "Delhi", "Mumbai", "07:30", "09:35", 6200, 42);
            insertFlight(ps, "AI-417", "SkyBridge Air", "Bengaluru", "Kolkata", "11:15", "13:50", 7100, 28);
            insertFlight(ps, "AI-509", "Nimbus Airlines", "Chennai", "Hyderabad", "15:05", "16:20", 4800, 34);
            insertFlight(ps, "AI-622", "Nimbus Airlines", "Mumbai", "Goa", "18:20", "19:30", 3900, 20);
            insertFlight(ps, "AI-731", "BlueOrbit", "Delhi", "Jaipur", "09:45", "10:50", 2800, 16);
        }
    }

    private static void insertFlight(
        PreparedStatement ps,
        String flightNumber,
        String airlineName,
        String source,
        String destination,
        String departureTime,
        String arrivalTime,
        double price,
        int seatsAvailable
    ) throws SQLException {
        ps.setString(1, flightNumber);
        ps.setString(2, airlineName);
        ps.setString(3, source);
        ps.setString(4, destination);
        ps.setString(5, departureTime);
        ps.setString(6, arrivalTime);
        ps.setDouble(7, price);
        ps.setInt(8, seatsAvailable);
        ps.executeUpdate();
    }

    private static void seedRoutes() throws SQLException {
        if (tableHasRows("routes")) {
            return;
        }

        String sql = "INSERT INTO routes (flight_number, stop_order, city) VALUES (?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            insertRoute(ps, "AI-203", 1, "Delhi");
            insertRoute(ps, "AI-203", 2, "Mumbai");
            insertRoute(ps, "AI-417", 1, "Bengaluru");
            insertRoute(ps, "AI-417", 2, "Nagpur");
            insertRoute(ps, "AI-417", 3, "Kolkata");
            insertRoute(ps, "AI-509", 1, "Chennai");
            insertRoute(ps, "AI-509", 2, "Hyderabad");
            insertRoute(ps, "AI-622", 1, "Mumbai");
            insertRoute(ps, "AI-622", 2, "Goa");
            insertRoute(ps, "AI-731", 1, "Delhi");
            insertRoute(ps, "AI-731", 2, "Jaipur");
        }
    }

    private static void insertRoute(PreparedStatement ps, String flightNumber, int stopOrder, String city) throws SQLException {
        ps.setString(1, flightNumber);
        ps.setInt(2, stopOrder);
        ps.setString(3, city);
        ps.executeUpdate();
    }

    private static boolean tableHasRows(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }
}
