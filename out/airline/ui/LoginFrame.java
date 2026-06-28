package airline.ui;

import airline.model.AppUser;
import airline.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthService();
    private final CardLayout formCards = new CardLayout();
    private final JPanel formPanel = new JPanel(formCards);

    private final JTextField loginUsernameField = new JTextField("admin");
    private final JPasswordField loginPasswordField = new JPasswordField("admin123");
    private final JTextField signupNameField = new JTextField();
    private final JTextField signupUsernameField = new JTextField();
    private final JPasswordField signupPasswordField = new JPasswordField();

    private final JButton loginTab = new JButton("Login");
    private final JButton signupTab = new JButton("Sign up");

    public LoginFrame() {
        setTitle("Airline Management Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1060, 680);
        setMinimumSize(new Dimension(940, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeroPanel(), BorderLayout.WEST);
        add(buildAuthPanel(), BorderLayout.CENTER);
        showLogin();
    }

    private JPanel buildHeroPanel() {
        GradientPanel panel = new GradientPanel();
        panel.setPreferredSize(new Dimension(440, getHeight()));
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(38, 38, 38, 38));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel badge = new JLabel("AIRLINE CONTROL");
        UIStyle.stylePill(badge, UIStyle.GOLD, UIStyle.NAVY);

        JLabel title = new JLabel("<html>Secure<br>Flight Desk</html>");
        UIStyle.styleLabel(title, 44, Color.WHITE, true);

        JLabel copy = new JLabel("<html>Authenticate staff, book passenger tickets, manage cancellations, and inspect live routes from one polished dashboard.</html>");
        UIStyle.styleLabel(copy, 16, new Color(232, 240, 255), false);

        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        status.setOpaque(false);
        status.add(metric("SQLite", "Database"));
        status.add(metric("Swing", "Frontend"));
        status.add(metric("Live", "Routes"));

        gbc.gridy = 0;
        panel.add(badge, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(34, 0, 0, 0);
        panel.add(title, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(18, 0, 0, 0);
        panel.add(copy, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(36, 0, 0, 0);
        panel.add(status, gbc);

        return panel;
    }

    private JPanel metric(String value, String label) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel valueLabel = new JLabel(value);
        JLabel textLabel = new JLabel(label);
        UIStyle.styleLabel(valueLabel, 18, Color.WHITE, true);
        UIStyle.styleLabel(textLabel, 12, new Color(224, 236, 255), false);
        panel.add(valueLabel, BorderLayout.NORTH);
        panel.add(textLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildAuthPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(248, 250, 252));
        wrapper.setBorder(BorderFactory.createEmptyBorder(48, 64, 48, 64));

        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(219, 228, 240), 1),
            BorderFactory.createEmptyBorder(34, 38, 34, 38)
        ));
        card.setPreferredSize(new Dimension(430, 500));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        JLabel heading = new JLabel("Welcome Back");
        UIStyle.styleLabel(heading, 30, new Color(8, 26, 57), true);
        JLabel subheading = new JLabel("Access your airline operations workspace.");
        UIStyle.styleLabel(subheading, 14, UIStyle.MUTED, false);
        header.add(heading, BorderLayout.NORTH);
        header.add(subheading, BorderLayout.SOUTH);

        JPanel tabs = new JPanel(new GridLayoutNoGap(1, 2));
        tabs.setOpaque(false);
        configureTab(loginTab);
        configureTab(signupTab);
        loginTab.addActionListener(event -> showLogin());
        signupTab.addActionListener(event -> showSignup());
        tabs.add(loginTab);
        tabs.add(signupTab);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(tabs, BorderLayout.SOUTH);

        formPanel.setOpaque(false);
        formPanel.add(buildLoginForm(), "login");
        formPanel.add(buildSignupForm(), "signup");

        card.add(top, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        wrapper.add(card);
        return wrapper;
    }

    private JPanel buildLoginForm() {
        JPanel form = verticalForm();
        form.add(label("Username"));
        UIStyle.styleField(loginUsernameField);
        form.add(loginUsernameField);
        form.add(Box.createVerticalStrut(16));
        form.add(label("Password"));
        UIStyle.stylePasswordField(loginPasswordField);
        form.add(loginPasswordField);
        form.add(Box.createVerticalStrut(24));

        JButton submit = primaryButton("Login to Dashboard");
        submit.addActionListener(event -> handleLogin());
        getRootPane().setDefaultButton(submit);
        form.add(submit);
        form.add(Box.createVerticalStrut(16));

        JLabel hint = new JLabel("Demo: admin/admin123 or staff/staff123");
        UIStyle.styleLabel(hint, 12, UIStyle.MUTED, false);
        form.add(hint);
        return form;
    }

    private JPanel buildSignupForm() {
        JPanel form = verticalForm();
        form.add(label("Full Name"));
        UIStyle.styleField(signupNameField);
        form.add(signupNameField);
        form.add(Box.createVerticalStrut(14));
        form.add(label("New Username"));
        UIStyle.styleField(signupUsernameField);
        form.add(signupUsernameField);
        form.add(Box.createVerticalStrut(14));
        form.add(label("Password"));
        UIStyle.stylePasswordField(signupPasswordField);
        form.add(signupPasswordField);
        form.add(Box.createVerticalStrut(24));

        JButton submit = primaryButton("Create Account");
        submit.addActionListener(event -> handleSignup());
        form.add(submit);
        return form;
    }

    private JPanel verticalForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        return form;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        UIStyle.styleLabel(label, 13, new Color(30, 41, 59), true);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        UIStyle.styleButton(button, new Color(0, 103, 184), Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return button;
    }

    private void configureTab(JButton button) {
        button.setUI(new BasicButtonUI());
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(false);
        button.setFont(UIStyle.titleFont(13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void showLogin() {
        formCards.show(formPanel, "login");
        setActiveTab(loginTab, true);
        setActiveTab(signupTab, false);
    }

    private void showSignup() {
        formCards.show(formPanel, "signup");
        setActiveTab(loginTab, false);
        setActiveTab(signupTab, true);
    }

    private void setActiveTab(JButton button, boolean active) {
        button.setBackground(active ? new Color(0, 103, 184) : new Color(236, 242, 249));
        button.setForeground(active ? Color.WHITE : new Color(30, 41, 59));
        button.setOpaque(true);
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        char[] password = loginPasswordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(this, "Enter username and password.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Optional<AppUser> user = authService.authenticate(username, password);
            Arrays.fill(password, '0');

            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            openDashboard(user.get());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignup() {
        String fullName = signupNameField.getText().trim();
        String username = signupUsernameField.getText().trim();
        char[] password = signupPasswordField.getPassword();

        try {
            AppUser newUser = authService.register(fullName, username, password);
            Arrays.fill(password, '0');
            JOptionPane.showMessageDialog(this, "Account created successfully.", "Signup Complete", JOptionPane.INFORMATION_MESSAGE);
            openDashboard(newUser);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Signup Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(AppUser user) {
        new AirlineManagementFrame(user).setVisible(true);
        dispose();
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(0, 0, new Color(0, 57, 107), getWidth(), getHeight(), new Color(0, 142, 151));
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 34));
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawOval(getWidth() - 210, 60, 260, 260);
            g2.drawOval(-120, getHeight() - 210, 260, 260);
            g2.dispose();
        }
    }

    private static class GridLayoutNoGap extends java.awt.GridLayout {
        GridLayoutNoGap(int rows, int cols) {
            super(rows, cols, 0, 0);
        }
    }
}