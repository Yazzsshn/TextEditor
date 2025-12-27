import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class TextEditor extends JFrame implements ActionListener {

    JTextPane textArea;
    JScrollPane scrollPane;
    JLabel fontLabel;
    JSpinner fontSizeSpinner;
    JButton fontColorButton;
    JComboBox<String> fontBox;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;
    JButton leftButton;
    JButton rightButton;
    JButton centerButton;
    JButton themeButton;
    boolean isDarkMode;
    JButton highLightButton;

    TextEditor(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Text Editor");
        this.setSize(600, 600);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textArea = new JTextPane();
        textArea.setPreferredSize(new Dimension(550, 450));
        textArea.setFont(new Font("Arial", Font.PLAIN,20));

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        fontLabel = new JLabel("Font : ");

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textArea.setFont(new Font(
                        textArea.getFont().getFamily(),
                        Font.PLAIN,
                        (int) fontSizeSpinner.getValue()
                ));
            }
        });

        fontColorButton = new JButton("Color");
        fontColorButton.addActionListener(this);

        String[] fonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        fontBox = new JComboBox<>(fonts);
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Arial");

        //--------------Menu Bar-----------------------
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        //--------------Menu Bar-----------------------

        leftButton = new JButton("Left");
        centerButton = new JButton("Center");
        rightButton = new JButton("Right");

        leftButton.addActionListener(this);
        centerButton.addActionListener(this);
        rightButton.addActionListener(this);

        themeButton = new JButton("DarkMode");
        themeButton.addActionListener(this);

        highLightButton = new JButton("HighLight the Text");
        highLightButton.addActionListener(this);

        this.setJMenuBar(menuBar);
        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(fontColorButton);
        this.add(fontBox);
        this.add(scrollPane);
        this.add(leftButton);
        this.add(centerButton);
        this.add(rightButton);
        this.add(themeButton);
        this.add(highLightButton);

        applyTheme();

        this.setVisible(true);
    }

    private void setAlignment(int alignment){
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setAlignment(attr, alignment);
        doc.setParagraphAttributes(0, doc.getLength(), attr, false);
    }
    private void applyTheme(){
        if (isDarkMode) {
            getContentPane().setBackground(new Color(45, 45, 45));
            textArea.setBackground(new Color(30, 30, 30));

            setTextColor(Color.WHITE);

            fontLabel.setForeground(Color.WHITE);
            themeButton.setText("Light Mode");
        } else {
            getContentPane().setBackground(Color.WHITE);
            textArea.setBackground(Color.WHITE);

            setTextColor(Color.BLACK);

            fontLabel.setForeground(Color.BLACK);
            themeButton.setText("Dark Mode");
        }
    }
    private void setTextColor(Color color){
        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        doc.setCharacterAttributes(0, doc.getLength(), attr, false);
    }
    private void highLightSelectedText(){
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();

        if (start == end) return;

        Color color = JColorChooser.showDialog(this, "Choose highlight color", Color.YELLOW);
        if (color == null) return;

        StyledDocument doc = textArea.getStyledDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setBackground(attr, color);

        doc.setCharacterAttributes(start, end - start, attr, false);

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == fontColorButton){
            Color color = JColorChooser.showDialog(null,"Choose a color", Color.black);
            if (color != null)
                setTextColor(color);
        }

        if (e.getSource() == fontBox){
            textArea.setFont(new Font(
                    (String) fontBox.getSelectedItem(),
                    Font.PLAIN,
                    textArea.getFont().getSize()
            ));
        }

        if (e.getSource() == openItem){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

            int response = fileChooser.showOpenDialog(null);
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                Scanner fileIn = null;
                try {
                    fileIn = new Scanner(file);
                    textArea.setText("");
                    while(fileIn.hasNextLine()){
                        textArea.setText(textArea.getText() + fileIn.nextLine() + "\n");
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } finally {
                    if (fileIn != null)
                        fileIn.close();
                }
            }
        }

        if (e.getSource() == saveItem){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);
            if (response == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                PrintWriter fileOut = null;
                try {
                    fileOut = new PrintWriter(file);
                    fileOut.print(textArea.getText());
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } finally {
                    if (fileOut != null)
                        fileOut.close();
                }
            }
        }

        if (e.getSource() == exitItem){
            System.exit(0);
        }

        if (e.getSource() == leftButton){
            setAlignment(StyleConstants.ALIGN_LEFT);
        }

        if (e.getSource() == centerButton){
            setAlignment(StyleConstants.ALIGN_CENTER);
        }

        if (e.getSource() == rightButton){
            setAlignment(StyleConstants.ALIGN_RIGHT);
        }
        if (e.getSource() == themeButton){
            isDarkMode = !isDarkMode;
            applyTheme();
        }
        if (e.getSource() == highLightButton){
            highLightSelectedText();
        }
    }
}
