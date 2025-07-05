import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Item {
    public String name;
    public String unit;
    public double availableStock;
    public List<Double> allowedQuantities;
    public double pricePerUnit;

    public Item(String name, String unit, double availableStock, 
               List<Double> allowedQuantities, double pricePerUnit) {
        this.name = name;
        this.unit = unit;
        this.availableStock = availableStock;
        this.allowedQuantities = allowedQuantities;
        this.pricePerUnit = pricePerUnit;
    }

    public static List<Item> loadItemsByCategory(int categoryId) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT name, unit, available_stock, price_per_unit FROM items WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                List<Double> quantities = new ArrayList<>();
                quantities.add(0.25);
                quantities.add(0.5);
                quantities.add(1.0);
                
                Item item = new Item(
                    rs.getString("name"),
                    rs.getString("unit"),
                    rs.getDouble("available_stock"),
                    quantities,
                    rs.getDouble("price_per_unit")
                );
                items.add(item);
            }
        }
        return items;
    }

    public void updateStock() throws SQLException {
        String sql = "UPDATE items SET available_stock = ? WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, this.availableStock);
            pstmt.setString(2, this.name);
            pstmt.executeUpdate();
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f %s @ Rs.%.2f)", name, availableStock, unit, pricePerUnit);
    }
}