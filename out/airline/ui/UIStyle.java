package airline.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public final class UIStyle {
    public static final Color NAVY = new Color(12, 27, 51);
    public static final Color SKY = new Color(22, 119, 255);
    public static final Color GOLD = new Color(255, 194, 63);
    public static final Color MIST = new Color(241, 245, 249);
    public static final Color INK = new Color(24, 32, 48);
    public static final Color SUCCESS = new Color(39, 174, 96);
    public static final Color DANGER = new Color(214, 48, 49);
    public static final Color MUTED = new Color(100, 116, 139);

    private static final Border FIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(210, 220, 235), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)
    );

    private UIStyle() {
    }

    public static Font titleFont(float size) {
        return new Font("Segoe UI Semibold", Font.BOLD, (int) size);
    }

    public static Font bodyFont(float size) {
        return new Font("Segoe UI", Font.PLAIN, (int) size);
    }

    public static void styleLabel(JLabel label, float size, Color color, boolean bold) {
        label.setFont(bold ? titleFont(size) : bodyFont(size));
        label.setForeground(color);
    }

    public static void styleField(JTextField field) {
        field.setFont(bodyFont(14));
        field.setBorder(FIELD_BORDER);
        field.setPreferredSize(new Dimension(220, 40));
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setFont(bodyFont(14));
        field.setBorder(FIELD_BORDER);
        field.setPreferredSize(new Dimension(220, 40));
    }

    public static void styleCombo(JComboBox<?> comboBox) {
        comboBox.setFont(bodyFont(14));
        comboBox.setBorder(FIELD_BORDER);
        comboBox.setPreferredSize(new Dimension(180, 38));
    }

    public static void styleButton(JButton button, Color background, Color foreground) {
        button.setUI(new BasicButtonUI());
        button.setFont(titleFont(14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setRolloverEnabled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }

    public static void stylePill(JLabel label, Color background, Color foreground) {
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(titleFont(12));
        label.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
    }

    public static void styleTable(JTable table) {
        table.setFont(bodyFont(13));
        table.setForeground(INK);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(225, 232, 240));
        table.setSelectionBackground(new Color(216, 231, 255));
        table.setSelectionForeground(INK);

        JTableHeader header = table.getTableHeader();
        header.setFont(titleFont(13));
        header.setBackground(new Color(0, 72, 135));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(0, 72, 135));
                label.setForeground(Color.WHITE);
                label.setFont(titleFont(13));
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(27, 94, 160)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
                return label;
            }
        });
    }
}