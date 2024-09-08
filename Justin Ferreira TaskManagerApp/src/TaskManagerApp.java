import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskManagerApp extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private TaskManagerLogic taskManagerLogic = new TaskManagerLogic();
    private List<Task<String>> taskList = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManagerApp app = new TaskManagerApp();
            app.setVisible(true);
        });
    }

    public TaskManagerApp() {
        setTitle("Task Manager");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Task Name", "Due Date", "Description", "Category", "Completed"}, 0);
        taskTable = new JTable(tableModel);
        addCheckboxColumn(taskTable);
        JScrollPane scrollPane = new JScrollPane(taskTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        taskManagerLogic.setupDatabase();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(34, 45, 34));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Task Name:");
        nameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JLabel dateLabel = new JLabel("Due Date:");
        dateLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(dateLabel, gbc);

        JTextField dateField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(descriptionLabel, gbc);

        JTextField descriptionField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(categoryLabel, gbc);

        JTextField categoryField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel inputButtonPanel = new JPanel();
        inputButtonPanel.setBackground(new Color(34, 45, 34));
        inputButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton addTaskButton = createStyledButton("Add Task");
        addTaskButton.addActionListener(e -> addTask(nameField, dateField, descriptionField, categoryField));
        inputButtonPanel.add(addTaskButton);

        JButton loadTasksButton = createStyledButton("Load Tasks");
        loadTasksButton.addActionListener(e -> loadTasks());
        inputButtonPanel.add(loadTasksButton);

        panel.add(inputButtonPanel, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBackground(new Color(34, 45, 34));

        JButton saveToFileButton = createStyledButton("Save to File");
        saveToFileButton.addActionListener(e -> taskManagerLogic.saveTasksToFile(getTasksFromTable()));
        panel.add(saveToFileButton);

        JButton loadFromFileButton = createStyledButton("Load from File");
        loadFromFileButton.addActionListener(e -> loadTasksFromFile());
        panel.add(loadFromFileButton);

        JButton showFilePropertiesButton = createStyledButton("Show File Properties");
        showFilePropertiesButton.addActionListener(e -> taskManagerLogic.showFileProperties("tasks.txt"));
        panel.add(showFilePropertiesButton);

        JButton exportToCSVButton = createStyledButton("Export to CSV");
        exportToCSVButton.addActionListener(e -> taskManagerLogic.exportTasksToCSV(getTasksFromTable()));
        panel.add(exportToCSVButton);

        JButton filterTasksButton = createStyledButton("Filter Tasks");
        filterTasksButton.addActionListener(e -> filterTasks());
        panel.add(filterTasksButton);

        JButton categorizeTasksButton = createStyledButton("Categorize Tasks");
        categorizeTasksButton.addActionListener(e -> categorizeTasks());
        panel.add(categorizeTasksButton);

        JButton updateStatusButton = createStyledButton("Update Status");
        updateStatusButton.addActionListener(e -> updateTaskStatus());
        panel.add(updateStatusButton);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void addCheckboxColumn(JTable table) {
    // Ensure the column index for the checkbox is correct (5 in this case)
    TableColumn checkboxColumn = table.getColumnModel().getColumn(5);

    // Set the cell renderer to display checkboxes
    checkboxColumn.setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JCheckBox checkbox = new JCheckBox();
            checkbox.setSelected((Boolean) value);
            return checkbox;
        }
    });

    // Set the cell editor to handle editing checkboxes
    checkboxColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()) {
        @Override
        public Object getCellEditorValue() {
                return ((JCheckBox) editorComponent).isSelected();
            }
        });
    }

    private void loadTasks() {
        taskList = taskManagerLogic.loadTasksFromDatabase();
        displayTasks(taskList);
    }

    private void displayTasks(List<Task<String>> tasks) {
        tableModel.setRowCount(0);
        for (Task<String> task : tasks) {
            tableModel.addRow(new Object[]{task.getId(), task.getTaskName(), task.getDueDate(), task.getDescription(), task.getCategory(), task.isCompleted()});
        }
    }

    private void loadTasksFromFile() {
        taskList = taskManagerLogic.loadTasksFromFile();
        displayTasks(taskList);
    }

    private List<Task<String>> getTasksFromTable() {
        int rowCount = tableModel.getRowCount();
        List<Task<String>> tasks = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            int id = Integer.parseInt(tableModel.getValueAt(i, 0).toString());
            String taskName = tableModel.getValueAt(i, 1).toString();
            String dueDate = tableModel.getValueAt(i, 2).toString();
            String description = tableModel.getValueAt(i, 3).toString();
            String category = tableModel.getValueAt(i, 4).toString();
            boolean completed = (Boolean) tableModel.getValueAt(i, 5);
            tasks.add(new Task<>(id, taskName, description, dueDate, completed, category));
        }
        return tasks;
    }

    private void filterTasks() {
        String keyword = JOptionPane.showInputDialog(this, "Enter keyword to filter tasks by name or category:");
        if (keyword != null && !keyword.isEmpty()) {
            List<Task<String>> filteredTasks = taskManagerLogic.filterTasks(taskList, keyword);
            displayTasks(filteredTasks);
        }
    }

    private void categorizeTasks() {
        Map<String, List<Task<String>>> categorizedTasks = taskManagerLogic.categorizeTasks(taskList);
        displayCategorizedTasks(categorizedTasks);
    }

    private void displayCategorizedTasks(Map<String, List<Task<String>>> categorizedTasks) {
        StringBuilder message = new StringBuilder("Categorized Tasks:\n\n");
        for (String category : categorizedTasks.keySet()) {
            message.append("Category: ").append(category).append("\n");
            for (Task<String> task : categorizedTasks.get(category)) {
                message.append(" - ").append(task.getTaskName()).append(" (Due: ").append(task.getDueDate()).append(")\n");
            }
            message.append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Categorized Tasks", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addTask(JTextField nameField, JTextField dateField, JTextField descriptionField, JTextField categoryField) {
        String taskName = nameField.getText();
        String dueDate = dateField.getText();
        String description = descriptionField.getText();
        String category = categoryField.getText();
        boolean completed = false;

        Task<String> task = new Task<>(taskList.size() + 1, taskName, description, dueDate, completed, category);
        taskManagerLogic.addTaskToDatabase(task);
        nameField.setText("");
        dateField.setText("");
        descriptionField.setText("");
        categoryField.setText("");
        loadTasks();
    }

    private void updateTaskStatus() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            boolean currentStatus = (boolean) tableModel.getValueAt(selectedRow, 5);
            boolean newStatus = !currentStatus;

            taskManagerLogic.updateTaskStatus(id, newStatus);
            tableModel.setValueAt(newStatus, selectedRow, 5);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to update its status.", "No Task Selected", JOptionPane.WARNING_MESSAGE);
        }
    }
}
