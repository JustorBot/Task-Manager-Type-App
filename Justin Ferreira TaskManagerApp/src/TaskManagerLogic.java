import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;
import java.util.Date;

public class TaskManagerLogic {
    private static final String DB_URL = "jdbc:sqlite:tasks.db";
    private Connection connection;

    public void setupDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                    "task_name TEXT NOT NULL," +
                                    "due_date TEXT," +
                                    "description TEXT," +
                                    "completed BOOLEAN," +
                                    "category TEXT)";
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addTaskToDatabase(Task<String> task) {
        String insertSQL = "INSERT INTO tasks (task_name, due_date, description, completed, category) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, task.getTaskName());
            pstmt.setString(2, task.getDueDate());
            pstmt.setString(3, task.getDescription());
            pstmt.setBoolean(4, task.isCompleted());
            pstmt.setString(5, task.getCategory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Task<String>> loadTasksFromDatabase() {
        List<Task<String>> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String taskName = rs.getString("task_name");
                String dueDate = rs.getString("due_date");
                String description = rs.getString("description");
                boolean completed = rs.getBoolean("completed");
                String category = rs.getString("category");
                tasks.add(new Task<>(id, taskName, description, dueDate, completed, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void saveTasksToFile(List<Task<String>> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"))) {
            for (Task<String> task : tasks) {
                writer.write(task.getId() + "," + task.getTaskName() + "," + task.getDueDate() + "," + task.getDescription() + "," + task.isCompleted() + "," + task.getCategory());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Task<String>> loadTasksFromFile() {
        List<Task<String>> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String taskName = parts[1];
                String dueDate = parts[2];
                String description = parts[3];
                boolean completed = Boolean.parseBoolean(parts[4]);
                String category = parts[5];
                tasks.add(new Task<>(id, taskName, description, dueDate, completed, category));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void showFileProperties(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            JOptionPane.showMessageDialog(null, "File: " + file.getName() + "\nSize: " + file.length() + " bytes\nLast Modified: " + new Date(file.lastModified()), "File Properties", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportTasksToCSV(List<Task<String>> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.csv"))) {
            writer.write("ID,Task Name,Due Date,Description,Completed,Category");
            writer.newLine();
            for (Task<String> task : tasks) {
                writer.write(task.getId() + "," + task.getTaskName() + "," + task.getDueDate() + "," + task.getDescription() + "," + task.isCompleted() + "," + task.getCategory());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Task<String>> filterTasks(List<Task<String>> tasks, String keyword) {
        List<Task<String>> filteredTasks = new ArrayList<>();
        for (Task<String> task : tasks) {
            if (task.getTaskName().contains(keyword) || task.getCategory().contains(keyword)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public Map<String, List<Task<String>>> categorizeTasks(List<Task<String>> tasks) {
        Map<String, List<Task<String>>> categorizedTasks = new HashMap<>();
        for (Task<String> task : tasks) {
            categorizedTasks.computeIfAbsent(task.getCategory(), k -> new ArrayList<>()).add(task);
        }
        return categorizedTasks;
    }

    public void updateTaskStatus(int id, boolean completed) {
        String updateSQL = "UPDATE tasks SET completed = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setBoolean(1, completed);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
