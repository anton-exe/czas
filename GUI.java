// import javafx.application.Application;
// import javafx.event.ActionEvent;
// import javafx.event.EventHandler;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.layout.StackPane;
// import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;

class GUI {
    // @Override
    // public void start(Stage primaryStage) {
    // System.out.println("in start()");
    // Button btn = new Button();
    // btn.setText("Say 'Hello World'");
    // btn.setOnAction(new EventHandler<ActionEvent>() {
    //
    // @Override
    // public void handle(ActionEvent event) {
    // System.out.println("Hello World!");
    // }
    // });
    //
    // StackPane root = new StackPane();
    // root.getChildren().add(btn);
    //
    // Scene scene = new Scene(root, 300, 250);
    //
    // primaryStage.setTitle("Hello World!");
    // primaryStage.setScene(scene);
    // primaryStage.show();
    // }

    private static String console = new String(new char[100]).replace("\0", "<br>");
    private static JFrame frame;
    private static JTextPane consoleArea;
    private static JScrollPane consoleScrollPane;

    public static void print(String text) {
        console += text.replaceAll("\n", "<br>");
        consoleArea.setText("<html>" + console + "</html>");

        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    public static void println(String text) {
        print(text + "\n");
    }

    public static void println() {
        print("\n");
    }

    public static void printf(String format, Object... args) {
        print(String.format(format, args));
    }

    public static void init() {
        // use GTK theme if possible
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        frame = new JFrame();

        // close (and later save) on quit
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MSPFAGame.exit();
            }
        });

        // main containing box
        Box frameBox = Box.createVerticalBox();
        frameBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // console
        consoleArea = new JTextPane();

        consoleArea.setBackground(new Color(0, 0, 0));
        consoleArea.setForeground(new Color(128, 255, 128));
        try {
            consoleArea
                    .setFont(Font.createFont(Font.TRUETYPE_FONT, GUI.class.getResourceAsStream("Monoid-Regular.ttf"))
                            .deriveFont(16f));
        } catch (FontFormatException e) {
            consoleArea.setFont(new Font("monospace", Font.PLAIN, 16));
        } catch (IOException e) {
        }
        consoleArea.setEditable(false);
        consoleArea.setContentType("text/html");

        // make scrollable
        consoleScrollPane = new JScrollPane(consoleArea);

        consoleScrollPane.setPreferredSize(new Dimension(768, 576));
        consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        Box consoleBox = Box.createHorizontalBox();
        consoleBox.add(consoleScrollPane);

        frameBox.add(consoleBox);

        JTextField inputLine = new JTextField();

        // process commands on enter
        Action inputAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                print("\n\n<font color=#aaccff>==> " + inputLine.getText() + "</font>\n\n");
                GUI.print("<font color=#ffffaa>");
                Parser.parseCmd(inputLine.getText());
                GUI.print("</font>");
                MSPFAGame.printInfo();
                inputLine.setText("");
            }
        };

        // constrain vertical size
        inputLine.setMaximumSize(new Dimension(100000, 24));
        inputLine.addActionListener(inputAction);

        // alternative submit button
        JButton inputButton = new JButton("==>");
        inputButton.addActionListener(inputAction);

        Box inputBox = Box.createHorizontalBox();
        inputBox.add(inputLine);
        inputBox.add(inputButton);

        frameBox.add(inputBox);

        frame.add(frameBox);

        frame.pack();

        frame.setSize(788, 646);

        frame.setVisible(true);

        inputLine.requestFocusInWindow();
    }
}
