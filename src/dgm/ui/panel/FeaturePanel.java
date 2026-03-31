package dgm.ui.panel;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class FeaturePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public FeaturePanel(String title, String description) {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(titleLabel, BorderLayout.NORTH);
        add(descriptionArea, BorderLayout.CENTER);
    }
}

