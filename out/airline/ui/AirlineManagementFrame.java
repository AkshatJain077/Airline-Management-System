package airline.ui;

import airline.model.Booking;
import airline.model.Flight;
import airline.model.RouteStop;
import airline.model.AppUser;
import airline.service.AirlineService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class AirlineManagementFrame extends JFrame {
    private final AirlineService service = new AirlineService();
    private final AppUser currentUser;

    private final DefaultTableModel flightsModel = new DefaultTableModel(
        new String[]{"Flight", "Airline", "From", "To", "Departure", "Arrival", "Price", "Seats"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel bookingsModel = new DefaultTableModel(
        new String[]{"Booking ID", "Passenger", "Email", "Flight", "Route", "Date", "Seats", "Fare", "Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable flightsTable = new JTable(flightsModel);
    private final JTable bookingsTable = new JTable(bookingsModel);
    private final JComboBox<String> flightSelector = new JComboBox<>();
    private final JComboBox<String> routeSelector = new JComboBox<>();
    private final JTextField passengerNameField = new JTextField();
    private final JTextField passengerEmailField = new JTextField();
    private final JSpinner seatCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JTextArea routeArea = new JTextArea();
    private final JLabel statsLabel = new JLabel();
    private final JLabel userLabel = new JLabel();

    public AirlineManagementFrame(AppUser currentUser) {
        this.currentUser = currentUser;
        setTitle("Airline Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildNavPanel(), BorderLayout.WEST);
        add(buildContentPanel(), BorderLayout.CENTER);

        refreshAllData();
    }

    private JPanel buildNavPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyle.NAVY);
        panel.setPreferredSize(new Dimension(240, getHeight()));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(28, 22, 28, 22));

        JLabel title = new JLabel("<html>Airline<br>Control Hub</html>");
        UIStyle.styleLabel(title, 28, Color.WHITE, true);

        JLabel subtitle = new JLabel("<html>Book tickets, review flights,<br>cancel bookings, and trace routes.</html>");
        UIStyle.styleLabel(subtitle, 14, new Color(214, 224, 240), false);

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));

        statsLabel.setVerticalAlignment(SwingConstants.TOP);
        UIStyle.styleLabel(statsLabel, 15, UIStyle.GOLD, true);
        panel.add(statsLabel);
        panel.add(Box.createVerticalGlue());

        JPanel profilePanel = new JPanel(new BorderLayout(0, 8));
        profilePanel.setOpaque(false);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(61, 79, 111), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        userLabel.setText("<html>" + currentUser.getFullName() + "<br><span style='color:#B8C6DA'>" + currentUser.getRole() + "</span></html>");
        UIStyle.styleLabel(userLabel, 13, Color.WHITE, true);

        JButton logoutButton = new JButton("Logout");
        UIStyle.styleButton(logoutButton, UIStyle.GOLD, UIStyle.NAVY);
        logoutButton.addActionListener(event -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        profilePanel.add(userLabel, BorderLayout.CENTER);
        profilePanel.add(logoutButton, BorderLayout.SOUTH);
        panel.add(profilePanel);
        return panel;
    }

    private JPanel buildContentPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIStyle.MIST);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 103, 184));
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel heading = new JLabel("Flight Operations Dashboard");
        UIStyle.styleLabel(heading, 27, Color.WHITE, true);

        JLabel subheading = new JLabel("Welcome, " + currentUser.getFullName() + ". Manage bookings, seats, and routes from one desk.");
        UIStyle.styleLabel(subheading, 14, new Color(223, 237, 255), false);

        header.add(heading, BorderLayout.NORTH);
        header.add(subheading, BorderLayout.SOUTH);

        JPanel cards = new JPanel(new GridLayout(2, 2, 18, 18));
        cards.setOpaque(false);
        cards.add(buildFlightsPanel());
        cards.add(buildBookingPanel());
        cards.add(buildBookingsPanel());
        cards.add(buildRoutesPanel());

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);
        headerWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        headerWrap.add(header, BorderLayout.CENTER);

        wrapper.add(headerWrap, BorderLayout.NORTH);
        wrapper.add(cards, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildFlightsPanel() {
        JPanel panel = createCardPanel("Available Flights", "Browse current routes, schedules, and seat inventory.");
        UIStyle.styleTable(flightsTable);
        setColumnWidths(flightsTable, 90, 130, 95, 105, 100, 90, 120, 70);
        panel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBookingPanel() {
        JPanel panel = createCardPanel("Book Ticket", "Select a flight, enter passenger details, and confirm instantly.");
        JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
        form.setOpaque(false);

        JLabel nameLabel = new JLabel("Passenger Name");
        JLabel emailLabel = new JLabel("Passenger Email");
        JLabel flightLabel = new JLabel("Flight Number");
        JLabel seatsLabel = new JLabel("Seats");
        UIStyle.styleLabel(nameLabel, 14, UIStyle.INK, true);
        UIStyle.styleLabel(emailLabel, 14, UIStyle.INK, true);
        UIStyle.styleLabel(flightLabel, 14, UIStyle.INK, true);
        UIStyle.styleLabel(seatsLabel, 14, UIStyle.INK, true);

        UIStyle.styleField(passengerNameField);
        UIStyle.styleField(passengerEmailField);
        UIStyle.styleCombo(flightSelector);
        seatCountSpinner.setFont(UIStyle.bodyFont(14));

        form.add(nameLabel);
        form.add(passengerNameField);
        form.add(emailLabel);
        form.add(passengerEmailField);
        form.add(flightLabel);
        form.add(flightSelector);
        form.add(seatsLabel);
        form.add(seatCountSpinner);

        JButton bookButton = new JButton("Confirm Booking");
        UIStyle.styleButton(bookButton, UIStyle.SUCCESS, Color.WHITE);
        bookButton.addActionListener(event -> handleBooking());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(bookButton);

        panel.add(form, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildBookingsPanel() {
        JPanel panel = createCardPanel("Manage Bookings", "Review reservations and cancel selected bookings.");
        UIStyle.styleTable(bookingsTable);
        setColumnWidths(bookingsTable, 90, 130, 170, 90, 140, 90, 70, 110, 100);

        JButton cancelButton = new JButton("Cancel Selected Booking");
        UIStyle.styleButton(cancelButton, UIStyle.DANGER, Color.WHITE);
        cancelButton.addActionListener(event -> handleCancellation());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(cancelButton);

        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private void setColumnWidths(JTable table, int... widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private JPanel buildRoutesPanel() {
        JPanel panel = createCardPanel("Route Explorer", "See the stop-by-stop journey for each flight.");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JLabel routeLabel = new JLabel("Choose Flight");
        UIStyle.styleLabel(routeLabel, 14, UIStyle.INK, true);
        UIStyle.styleCombo(routeSelector);

        JButton showButton = new JButton("Show Route");
        UIStyle.styleButton(showButton, UIStyle.SKY, Color.WHITE);
        showButton.addActionListener(event -> showSelectedRoute());

        top.add(routeLabel);
        top.add(routeSelector);
        top.add(showButton);

        routeArea.setEditable(false);
        routeArea.setLineWrap(true);
        routeArea.setWrapStyleWord(true);
        routeArea.setFont(UIStyle.bodyFont(15));
        routeArea.setBackground(new Color(249, 251, 255));
        routeArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 232, 240), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(routeArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCardPanel(String titleText, String subtitleText) {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(225, 232, 240), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel(titleText);
        JLabel subtitle = new JLabel(subtitleText);
        UIStyle.styleLabel(title, 18, UIStyle.INK, true);
        UIStyle.styleLabel(subtitle, 13, new Color(109, 126, 154), false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private void refreshAllData() {
        List<Flight> flights = service.getFlights();
        List<Booking> bookings = service.getBookings();
        populateFlightsTable(flights);
        populateBookingsTable(bookings);
        populateSelectors(flights);
        updateStats(flights, bookings);
        if (routeSelector.getItemCount() > 0) {
            routeSelector.setSelectedIndex(0);
            showSelectedRoute();
        }
    }

    private void populateFlightsTable(List<Flight> flights) {
        flightsModel.setRowCount(0);
        for (Flight flight : flights) {
            flightsModel.addRow(new Object[]{
                flight.getFlightNumber(),
                flight.getAirlineName(),
                flight.getSource(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                String.format("INR %.2f", flight.getPrice()),
                flight.getSeatsAvailable()
            });
        }
    }

    private void populateBookingsTable(List<Booking> bookings) {
        bookingsModel.setRowCount(0);
        for (Booking booking : bookings) {
            bookingsModel.addRow(new Object[]{
                booking.getId(),
                booking.getPassengerName(),
                booking.getPassengerEmail(),
                booking.getFlightNumber(),
                booking.getSource() + " -> " + booking.getDestination(),
                booking.getBookingDate(),
                booking.getSeatCount(),
                String.format("INR %.2f", booking.getTotalFare()),
                booking.getStatus()
            });
        }
    }

    private void populateSelectors(List<Flight> flights) {
        DefaultComboBoxModel<String> flightModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> routeModel = new DefaultComboBoxModel<>();
        for (Flight flight : flights) {
            flightModel.addElement(flight.getFlightNumber());
            routeModel.addElement(flight.getFlightNumber());
        }
        flightSelector.setModel(flightModel);
        routeSelector.setModel(routeModel);
    }

    private void updateStats(List<Flight> flights, List<Booking> bookings) {
        long activeBookings = bookings.stream().filter(booking -> "CONFIRMED".equalsIgnoreCase(booking.getStatus())).count();
        int totalSeats = flights.stream().mapToInt(Flight::getSeatsAvailable).sum();
        statsLabel.setText("""
            <html>
            Live Desk Status<br><br>
            Flights online: %d<br>
            Confirmed tickets: %d<br>
            Open seats: %d
            </html>
            """.formatted(flights.size(), activeBookings, totalSeats));
    }

    private void handleBooking() {
        try {
            String passengerName = passengerNameField.getText().trim();
            String passengerEmail = passengerEmailField.getText().trim();
            String flightNumber = (String) flightSelector.getSelectedItem();
            int seatCount = (Integer) seatCountSpinner.getValue();

            if (passengerName.isEmpty() || passengerEmail.isEmpty() || flightNumber == null) {
                throw new IllegalArgumentException("Please complete all booking fields.");
            }
            if (!passengerEmail.contains("@")) {
                throw new IllegalArgumentException("Please enter a valid email address.");
            }

            service.createBooking(passengerName, passengerEmail, flightNumber, seatCount);
            passengerNameField.setText("");
            passengerEmailField.setText("");
            seatCountSpinner.setValue(1);
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Booking confirmed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancellation() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (Integer) bookingsModel.getValueAt(row, 0);
        try {
            service.cancelBooking(bookingId);
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectedRoute() {
        String flightNumber = (String) routeSelector.getSelectedItem();
        if (flightNumber == null) {
            routeArea.setText("No route selected.");
            return;
        }

        List<RouteStop> stops = service.getRouteStops(flightNumber);
        if (stops.isEmpty()) {
            routeArea.setText("No route information available for " + flightNumber + ".");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Flight ").append(flightNumber).append("\n\n");
        builder.append("Journey path:\n");
        for (int i = 0; i < stops.size(); i++) {
            builder.append(i + 1).append(". ").append(stops.get(i).getCity()).append("\n");
        }

        builder.append("\nVisual route:\n");
        for (int i = 0; i < stops.size(); i++) {
            builder.append(stops.get(i).getCity());
            if (i < stops.size() - 1) {
                builder.append(" -> ");
            }
        }

        routeArea.setText(builder.toString());
        routeArea.setCaretPosition(0);
    }
}
