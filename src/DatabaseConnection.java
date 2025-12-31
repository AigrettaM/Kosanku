import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Class untuk mengelola koneksi database
 * @author Lenovo
 */
public class DatabaseConnection {
    
    // Konfigurasi database
    private static final String URL = "jdbc:mysql://localhost:3306/kosanku";
    private static final String USERNAME = "root"; // sesuaikan dengan username MySQL Anda
    private static final String PASSWORD = ""; // sesuaikan dengan password MySQL Anda
    
    private static Connection connection;
    
    /**
     * Method untuk mendapatkan koneksi database
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Membuat koneksi
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Koneksi database berhasil!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver tidak ditemukan!\nPastikan mysql-connector-java sudah ditambahkan ke classpath.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Gagal melakukan koneksi database!");
            JOptionPane.showMessageDialog(null, 
                "Gagal melakukan koneksi database!\nPesan error: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Method untuk menutup koneksi database
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi database!");
            e.printStackTrace();
        }
    }
    
    /**
     * Method untuk test koneksi database
     * @return true jika koneksi berhasil, false jika gagal
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
