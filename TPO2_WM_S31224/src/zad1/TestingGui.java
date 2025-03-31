package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestingGui extends JFrame {
    private JPanel mainPanel;
    private JTextField Country;
    private JButton button1;
    private JList Weather;
    private JPanel webPanel;
    private JTextField Town;
    private JFXPanel fxPanel;


    public TestingGui() {
        fxPanel = new JFXPanel();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(mainPanel);
        Platform.runLater(this::createJFXContent);
        webPanel.add(fxPanel);
        this.pack();

        button1.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {



            }
        });
    }

    private void createJFXContent() {
        WebView webView = new WebView();
        webView.getEngine().load("https://en.wikipedia.org/wiki/" + Country.getText()); //+ ",_" + textField2.getText()
        Scene scene = new Scene(webView);
        fxPanel.setScene(scene);
        this.pack();
    }






    public static void main(String[] args) {



        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();


        // ...
        // część uruchamiająca GUI


        SwingUtilities.invokeLater(TestingGui::new);

    }

}

class Weather{
    Main main;
}

class Main{
    double temp;
    double feels_like;
    double temp_min;
    double temp_max;
    double pressure;
    double humidity;
}


