import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER = "libraryuser";
    private static final String PASSWORD = "lib123";
    // change if needed

    private Connection conn;
    private Scanner sc;

    public LibraryManagementSystem() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            sc = new Scanner(System.in);
            System.out.println("✅ Connected to Database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add new book
    public void addBook() {
        try {
            System.out.print("Enter Book Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();

            String sql = "INSERT INTO Books (title, author) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.executeUpdate();
            System.out.println("📚 Book Added Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all books
    public void viewBooks() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Books");
            System.out.println("\n---- Books List ----");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("book_id") +
                        ", Title: " + rs.getString("title") +
                        ", Author: " + rs.getString("author") +
                        ", Available: " + rs.getBoolean("available"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add User
    public void addUser() {
        try {
            System.out.print("Enter User Name: ");
            String name = sc.nextLine();

            String sql = "INSERT INTO Users (name) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("👤 User Added Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all users
    public void viewUsers() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Users");
            System.out.println("\n---- Users List ----");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("user_id") +
                        ", Name: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Borrow a book
    public void borrowBook() {
        try {
            System.out.print("Enter User ID: ");
            int userId = sc.nextInt();
            System.out.print("Enter Book ID: ");
            int bookId = sc.nextInt();
            sc.nextLine();

            // Check availability
            String checkSql = "SELECT available FROM Books WHERE book_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getBoolean("available")) {
                // Insert into Transactions
                String sql = "INSERT INTO Transactions (user_id, book_id) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setInt(2, bookId);
                ps.executeUpdate();

                // Update book availability
                String updateSql = "UPDATE Books SET available=false WHERE book_id=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();

                System.out.println("✅ Book Borrowed Successfully!");
            } else {
                System.out.println("❌ Book Not Available.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Return a book
    public void returnBook() {
        try {
            System.out.print("Enter Transaction ID: ");
            int transId = sc.nextInt();
            sc.nextLine();

            // Get book_id from transaction
            String sql = "SELECT book_id FROM Transactions WHERE transaction_id=? AND return_date IS NULL";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, transId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");

                // Update transaction return_date
                String updateTrans = "UPDATE Transactions SET return_date=NOW() WHERE transaction_id=?";
                PreparedStatement psUpdate = conn.prepareStatement(updateTrans);
                psUpdate.setInt(1, transId);
                psUpdate.executeUpdate();

                // Mark book available again
                String updateBook = "UPDATE Books SET available=true WHERE book_id=?";
                PreparedStatement psBook = conn.prepareStatement(updateBook);
                psBook.setInt(1, bookId);
                psBook.executeUpdate();

                System.out.println("📖 Book Returned Successfully!");
            } else {
                System.out.println("❌ Invalid Transaction ID or Book Already Returned.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menu
    public void menu() {
        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Add User");
            System.out.println("4. View Users");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. Exit");
            System.out.print("Choose Option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewBooks();
                case 3 -> addUser();
                case 4 -> viewUsers();
                case 5 -> borrowBook();
                case 6 -> returnBook();
                case 7 -> {
                    System.out.println("👋 Exiting...");
                    return;
                }
                default -> System.out.println("❌ Invalid Choice.");
            }
        }
    }

    public static void main(String[] args) {
        LibraryManagementSystem app = new LibraryManagementSystem();
        app.menu();
    }
}
