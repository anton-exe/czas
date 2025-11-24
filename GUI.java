import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;

import javax.swing.*;

class GUI {
    private static String console = new String(new char[100]).replace("\0", "<br>");
    private static JFrame frame;
    private static JTextPane consoleArea;
    private static JScrollPane consoleScrollPane;
    private static Box inventoryBox;

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

    public static void backspace(int chars) {
        console = console.substring(0, console.length() - chars);
        consoleArea.setText("<html>" + console + "</html>");

        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    public static void backspace() {
        backspace(1);
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

        /*
         * we want something like this
         * ┌─────────────────────┬──────┐
         * │·····················│······│
         * │·····················│·Map··│
         * │·······Console·······│······│
         * │·····················├──────┤
         * ├──────────────┬──────┤······│
         * │··Text·Input··│·Send·│·Inv.·│
         * ├──────────┬───┴──────┤······│
         * │·Shortcut·│·Shortcut·│······│
         * └──────────┴──────────┴──────┘
         */
        frame = new JFrame();

        // close (and later save) on quit
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Game.exit();
            }
        });

        // main containing box
        Box frameBox = Box.createHorizontalBox();
        frameBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.add(frameBox);

        Box leftBox = Box.createVerticalBox();
        Box rightBox = Box.createVerticalBox();

        frameBox.add(leftBox);
        frameBox.add(rightBox);

        // {{ LEFT SIDE
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

        leftBox.add(consoleBox);

        // text field
        JTextField inputLine = new JTextField();

        // process commands on enter
        Action inputAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand(inputLine.getText());
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

        leftBox.add(inputBox);

        // shortcuts
        Box shortcutBox = Box.createHorizontalBox();

        JButton invButton = new JButton("inventory");
        invButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand("inv");
            }
        });
        shortcutBox.add(invButton);

        JButton lookButton = new JButton("look");
        lookButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCommand("look");
            }
        });
        shortcutBox.add(lookButton);

        leftBox.add(shortcutBox);

        // }}
        // {{ RIGHT SIDE
        // map
        JTextPane mapArea = new JTextPane();
        mapArea.setBackground(new Color(0, 0, 0));
        mapArea.setForeground(new Color(128, 255, 128));
        try {
            mapArea
                    .setFont(Font.createFont(Font.TRUETYPE_FONT, GUI.class.getResourceAsStream("Monoid-Regular.ttf"))
                            .deriveFont(16f));
        } catch (FontFormatException e) {
            mapArea.setFont(new Font("monospace", Font.PLAIN, 16));
        } catch (IOException e) {
        }
        mapArea.setEditable(false);
        mapArea.setPreferredSize(new Dimension(256, 384));
        mapArea.setMaximumSize(new Dimension(512, 768));

        rightBox.add(mapArea);

        inventoryBox = Box.createVerticalBox();

        JScrollPane inventoryScroll = new JScrollPane(inventoryBox);

        inventoryScroll.setBackground(new Color(164, 164, 164));
        inventoryScroll.setPreferredSize(new Dimension(256, 256));

        rightBox.add(inventoryScroll);

        // inventory

        // }}

        frame.pack();

        frame.setSize(1044, 646);

        frame.setVisible(true);

        inputLine.requestFocusInWindow();
    }

    public static void rerenderInventory() {
        inventoryBox.removeAll();
        for (Item item : Game.getPlayer().getInventory()) {
            try {
                Box itemBox = Box.createHorizontalBox();

                itemBox.setMaximumSize(new Dimension(100000, 32));

                JLabel picLabel = new JLabel(
                        new ImageIcon(item.getIcon().getScaledInstance(32, 32, java.awt.Image.SCALE_SMOOTH)));
                JTextArea itemName = new JTextArea(item.getName());

                itemName.setEditable(false);
                itemName.setFont(new Font("monospace", Font.PLAIN, 24));

                itemBox.add(picLabel);
                itemBox.add(itemName);

                inventoryBox.add(itemBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // frame.pack();
        SwingUtilities.updateComponentTreeUI(inventoryBox);
    }

    private static void runCommand(String cmd) {
        print("\n\n<font color=#aaccff>==> " + cmd + "</font>\n\n");
        print("<font color=#ffffaa>");
        Parser.parseCmd(cmd);
        print("</font>");
        Game.printInfo();
    }
}
