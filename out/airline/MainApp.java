package airline;

import airline.db.DatabaseManager;
import airline.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            DatabaseManager.initialize();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
