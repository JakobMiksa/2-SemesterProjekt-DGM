package dgm.app;

import javax.swing.SwingUtilities;

import dgm.controller.AppController;
import dgm.ui.MainFrame;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppController controller = new AppController();
                MainFrame frame = new MainFrame(controller);
                frame.setVisible(true);
            }
        });
    }
}

