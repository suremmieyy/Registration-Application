import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CombinedApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationForm().setVisible(true);
            }
        });
    }

    static class RegistrationForm extends JFrame {
        private JTextField nameField, mobileField, dobField, addressField;
        private JRadioButton maleButton, femaleButton;
        private ButtonGroup genderGroup;
        private Connection connection;

        public RegistrationForm() {
            // Set up the JFrame
            setTitle("Registration Form");
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(7, 2));

            // Add form fields
            add(new JLabel("Name:"));
            nameField = new JTextField();
            add(nameField);

            add(new JLabel("Mobile:"));
            mobileField = new JTextField();
            add(mobileField);

            add(new JLabel("Gender:"));
            maleButton = new JRadioButton("Male");
            femaleButton = new JRadioButton("Female");
            genderGroup = new ButtonGroup();
            genderGroup.add(maleButton);
            genderGroup.add(femaleButton);
            JPanel genderPanel = new JPanel();
            genderPanel.add(maleButton);
            genderPanel.add(femaleButton);
            add(genderPanel);

            add(new JLabel("DOB (yyyy-mm-dd):"));
            dobField = new JTextField();
            add(dobField);

            add(new JLabel("Address:"));
            addressField = new JTextField();
            add(addressField);

            // Create button panel
            JPanel buttonPanel = new JPanel();
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveData();
                    new DataTable().setVisible(true); // Transition to the second phase
                    dispose(); // Close the current frame
                }
            });
            buttonPanel.add(submitButton);

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resetForm();
                }
            });
            buttonPanel.add(resetButton);

            add(buttonPanel);

            // Initialize the database connection and create table if it doesn't exist
            initializeDatabase();
        }

        private void initializeDatabase() {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "Ngunusamuel-0225");
                Statement statement = connection.createStatement();
                String createTableQuery = "CREATE TABLE IF NOT EXISTS registration (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "mobile VARCHAR(20), " +
                        "gender VARCHAR(10), " +
                        "dob DATE, " +
                        "address VARCHAR(255))";
                statement.execute(createTableQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void saveData() {
            String name = nameField.getText();
            String mobile = mobileField.getText();
            String gender = maleButton.isSelected() ? "Male" : "Female";
            String dob = dobField.getText();
            String address = addressField.getText();

            // Validate and parse date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate;
            try {
                parsedDate = dateFormat.parse(dob);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-mm-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String query = "INSERT INTO registration (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, mobile);
                statement.setString(3, gender);
                statement.setDate(4, new java.sql.Date(parsedDate.getTime()));
                statement.setString(5, address);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void resetForm() {
            nameField.setText("");
            mobileField.setText("");
            dobField.setText("");
            addressField.setText("");
            genderGroup.clearSelection();
        }
    }

    static class DataTable extends JFrame {
        private JTextField nameField, mobileField, dobField, addressField;
        private JRadioButton maleButton, femaleButton;
        private ButtonGroup genderGroup;
        private JTable dataTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public DataTable() {
            // Set up the JFrame
            setTitle("Data Table");
            setSize(800, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // Create form panel
            JPanel formPanel = new JPanel(new GridLayout(7, 2));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add form fields
            formPanel.add(new JLabel("Name:"));
            nameField = new JTextField();
            formPanel.add(nameField);

            formPanel.add(new JLabel("Mobile:"));
            mobileField = new JTextField();
            formPanel.add(mobileField);

            formPanel.add(new JLabel("Gender:"));
            maleButton = new JRadioButton("Male");
            femaleButton = new JRadioButton("Female");
            genderGroup = new ButtonGroup();
            genderGroup.add(maleButton);
            genderGroup.add(femaleButton);
            JPanel genderPanel = new JPanel();
            genderPanel.add(maleButton);
            genderPanel.add(femaleButton);
            formPanel.add(genderPanel);

            formPanel.add(new JLabel("DOB (yyyy-mm-dd):"));
            dobField = new JTextField();
            formPanel.add(dobField);

            formPanel.add(new JLabel("Address:"));
            addressField = new JTextField();
            formPanel.add(addressField);

            // Create button panel
            JPanel buttonPanel = new JPanel();
            JButton reportButton = new JButton("Report");
            reportButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadData();
                }
            });
            buttonPanel.add(reportButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            buttonPanel.add(exitButton);

            formPanel.add(buttonPanel);

            // Create the data table
            String[] columns = {"ID", "Name", "Mobile", "Gender", "DOB", "Address"};
            tableModel = new DefaultTableModel(columns, 0);
            dataTable = new JTable(tableModel);

            // Add form panel and data table to the frame
            add(formPanel, BorderLayout.NORTH);
            add(new JScrollPane(dataTable), BorderLayout.CENTER);

            // Initialize the database connection
            initializeDatabase();
        }

        private void initializeDatabase() {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration_db", "root", "Ngunusamuel-0225");
                Statement statement = connection.createStatement();
                String createTableQuery = "CREATE TABLE IF NOT EXISTS registration (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "mobile VARCHAR(20), " +
                        "gender VARCHAR(10), " +
                        "dob DATE, " +
                        "address VARCHAR(255))";
                statement.execute(createTableQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void loadData() {
            try {
                String query = "SELECT * FROM registration";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                tableModel.setRowCount(0);
                while (resultSet.next()) {
                    Object[] row = {
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("mobile"),
                            resultSet.getString("gender"),
                            resultSet.getDate("dob").toString(),
                            resultSet.getString("address")
                    };
                    tableModel.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
