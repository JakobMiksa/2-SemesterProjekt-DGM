package dgm.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import dgm.controller.AppController;
import dgm.ui.panel.FeaturePanel;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame(AppController controller) {
        setTitle("Den Glade Bondemand");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);

        getRootPane().setBorder(new EmptyBorder(16, 16, 16, 16));
        setLayout(new BorderLayout(0, 16));

        JLabel header = new JLabel("Den Glade Bondemand", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 24f));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Lager", new FeaturePanel(
                "Lagerstyring",
                "Her kan I senere vise lagerstatus, antal varer og søgning."));
        tabs.addTab("Salg", new FeaturePanel(
                "Salgsregistrering",
                "Her kan I senere registrere salg og opdatere lageret automatisk."));
        tabs.addTab("Reservationer", new FeaturePanel(
                "Reservationer",
                "Her kan I senere håndtere reservationer og betaling på forhånd."));

        JLabel footer = new JLabel(controller.getSystemStatus());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
}

