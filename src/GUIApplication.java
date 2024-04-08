import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GUIApplication extends JFrame {
    private final JComboBox<String> classComboBox;
    private final JTextArea taskTextArea;
    private final JTextArea outputTextArea;
    private final DefaultListModel<String> taskListModel;
    private final List<StatusListener> statusListeners;
    private ClassLoaderUtil classLoaderUtil;

    public GUIApplication() {
        JLabel classLabel = new JLabel("Wybierz klasę:");
        classComboBox = new JComboBox<>();
        JButton loadClassButton = new JButton("Załaduj klasę");
        JButton executeButton = new JButton("Wykonaj zadanie");

        taskTextArea = new JTextArea(5, 20);
        taskTextArea.setLineWrap(true);
        taskTextArea.setWrapStyleWord(true);
        JScrollPane taskScrollPane = new JScrollPane(taskTextArea);

        outputTextArea = new JTextArea(10, 20);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2));
        mainPanel.add(classLabel);
        mainPanel.add(classComboBox);
        mainPanel.add(loadClassButton);
        mainPanel.add(new JLabel()); // Empty space
        mainPanel.add(taskScrollPane);
        mainPanel.add(executeButton);

        taskListModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskListModel);
        JScrollPane taskListScrollPane = new JScrollPane(taskList);
        taskListScrollPane.setPreferredSize(new Dimension(300, 100));

        statusListeners = new ArrayList<>();
        statusListeners.add(new StatusListener() {
            @Override
            public void onStatusUpdate(Status status) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        taskListModel.addElement(status.getMessage());
                    }
                });
            }
        });

        JPanel monitorPanel = new JPanel(new BorderLayout());
        monitorPanel.add(new JLabel("Monitor zadań:"), BorderLayout.NORTH);
        monitorPanel.add(taskListScrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);
        add(monitorPanel, BorderLayout.CENTER);
        add(outputScrollPane, BorderLayout.SOUTH);

        loadClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Logika ładowania klas - pominięta dla czytelności
                // Wczytywanie dostępnych klas do ComboBoxa
                classComboBox.addItem("SumProcessor");
                classComboBox.addItem("UpperCaseProcessor");
                classComboBox.addItem("ReverseProcessor");
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskTextArea.getText();
                String className = (String) classComboBox.getSelectedItem();
                executeTask(task, className);
            }
        });

        JButton unloadClassButton = new JButton("Wyładuj klasę");
        mainPanel.add(unloadClassButton);

        unloadClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String className = (String) classComboBox.getSelectedItem();
                if (className != null) {
                    try {
                        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                        Class<?> clazz = classLoader.loadClass(className);
                        ClassUnloader.unloadClass(clazz);
                        classComboBox.removeItem(className); // Remove from combo box
                        JOptionPane.showMessageDialog(null, "Klasa " + className + " została wyładowana.");
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "Błąd podczas ładowania klasy.");
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Proszę wybrać klasę do wyładowania.");
                }
            }
        });
        setTitle("lab04");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void updateClassComboBox() {

    }

    private void executeTask(String task, String className) {
        TaskExecutor taskExecutor = new TaskExecutor(task, className, statusListeners);
        new Thread(taskExecutor).start();
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUIApplication();
            }
        });
    }
}
