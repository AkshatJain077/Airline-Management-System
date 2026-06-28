package airline.service;

import airline.db.DatabaseManager;
import airline.model.Booking;
import airline.model.Flight;
import airline.model.RouteStop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AirlineService {
    public List<Flight> getFlights() {
        List<Flight> flights = new ArrayList<>();
        String sql = """
            SELECT id, flight_number, airline_name, source, destination, departure_time, arrival_time, price, seats_available
            FROM flights
            ORDER BY departure_time
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                flights.add(mapFlight(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to load flights.", ex);
        }
        return flights;
    }

    public List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT id, passenger_name, passenger_email, flight_number, source, destination, booking_date, seat_count, total_fare, status
            FROM bookings
            ORDER BY id DESC
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to load bookings.", ex);
        }
        return bookings;
    }

    public List<RouteStop> getRouteStops(String flightNumber) {
        List<RouteStop> routeStops = new ArrayList<>();
        String sql = """
            SELECT flight_number, stop_order, city
            FROM routes
            WHERE flight_number = ?
            ORDER BY stop_order
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, flightNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    routeStops.add(new RouteStop(
                        rs.getString("flight_number"),
                        rs.getInt("stop_order"),
                        rs.getString("city")
                    ));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to load route details.", ex);
        }
        return routeStops;
    }

    public void createBooking(String passengerName, String passengerEmail, String flightNumber, int seatCount) {
        String flightSql = """
            SELECT source, destination, price, seats_available
            FROM flights
            WHERE flight_number = ?
            """;
        String insertBookingSql = """
            INSERT INTO bookings
            (passenger_name, passenger_email, flight_number, source, destination, booking_date, seat_count, total_fare, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        String updateSeatsSql = "UPDATE flights SET seats_available = seats_available - ? WHERE flight_number = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            String source;
            String destination;
            double price;
            int seatsAvailable;

            try (PreparedStatement flightPs = connection.prepareStatement(flightSql)) {
                flightPs.setString(1, flightNumber);
                try (ResultSet rs = flightPs.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Selected flight does not exist.");
                    }
                    source = rs.getString("source");
                    destination = rs.getString("destination");
                    price = rs.getDouble("price");
                    seatsAvailable = rs.getInt("seats_available");
                }
            }

            if (seatCount <= 0) {
                throw new IllegalArgumentException("Seat count must be at least 1.");
            }
            if (seatCount > seatsAvailable) {
                throw new IllegalArgumentException("Not enough seats available for this flight.");
            }

            try (PreparedStatement bookingPs = connection.prepareStatement(insertBookingSql);
                 PreparedStatement seatPs = connection.prepareStatement(updateSeatsSql)) {
                bookingPs.setString(1, passengerName);
                bookingPs.setString(2, passengerEmail);
                bookingPs.setString(3, flightNumber);
                bookingPs.setString(4, source);
                bookingPs.setString(5, destination);
                bookingPs.setString(6, LocalDate.now().toString());
                bookingPs.setInt(7, seatCount);
                bookingPs.setDouble(8, price * seatCount);
                bookingPs.setString(9, "CONFIRMED");
                bookingPs.executeUpdate();

                seatPs.setInt(1, seatCount);
                seatPs.setString(2, flightNumber);
                seatPs.executeUpdate();
            }

            connection.commit();
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to create booking.", ex);
        }
    }

    public void cancelBooking(int bookingId) {
        String bookingSql = "SELECT flight_number, seat_count, status FROM bookings WHERE id = ?";
        String cancelSql = "UPDATE bookings SET status = ? WHERE id = ?";
        String restoreSeatSql = "UPDATE flights SET seats_available = seats_available + ? WHERE flight_number = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);

            String flightNumber;
            int seatCount;
            String status;

            try (PreparedStatement bookingPs = connection.prepareStatement(bookingSql)) {
                bookingPs.setInt(1, bookingId);
                try (ResultSet rs = bookingPs.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Booking not found.");
                    }
                    flightNumber = rs.getString("flight_number");
                    seatCount = rs.getInt("seat_count");
                    status = rs.getString("status");
                }
            }

            if ("CANCELLED".equalsIgnoreCase(status)) {
                throw new IllegalArgumentException("This booking is already cancelled.");
            }

            try (PreparedStatement cancelPs = connection.prepareStatement(cancelSql);
                 PreparedStatement restorePs = connection.prepareStatement(restoreSeatSql)) {
                cancelPs.setString(1, "CANCELLED");
                cancelPs.setInt(2, bookingId);
                cancelPs.executeUpdate();

                restorePs.setInt(1, seatCount);
                restorePs.setString(2, flightNumber);
                restorePs.executeUpdate();
            }

            connection.commit();
        } catch (SQLException ex) {
            throw new IllegalStateException("Unable to cancel booking.", ex);
        }
    }

    private Flight mapFlight(ResultSet rs) throws SQLException {
        return new Flight(
            rs.getInt("id"),
            rs.getString("flight_number"),
            rs.getString("airline_name"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getString("departure_time"),
            rs.getString("arrival_time"),
            rs.getDouble("price"),
            rs.getInt("seats_available")
        );
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        return new Booking(
            rs.getInt("id"),
            rs.getString("passenger_name"),
            rs.getString("passenger_email"),
            rs.getString("flight_number"),
            rs.getString("source"),
            rs.getString("destination"),
            rs.getString("booking_date"),
            rs.getInt("seat_count"),
            rs.getDouble("total_fare"),
            rs.getString("status")
        );
    }
}
