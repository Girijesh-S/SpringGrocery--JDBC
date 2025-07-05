import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Category {
    public String name;
    public List<Item> items = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public static List<Category> loadAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM categories";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Category category = new Category(rs.getString("name"));
                category.items = Item.loadItemsByCategory(rs.getInt("id"));
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public String toString() {
        return name;
    }
}