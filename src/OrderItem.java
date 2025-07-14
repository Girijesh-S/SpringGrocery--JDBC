import java.sql.*;

public class OrderItem {
    public Item item;
    public double quantityOrdered;

    public OrderItem(Item item, double quantityOrdered) {
        this.item = item;
        this.quantityOrdered = quantityOrdered;
    }

    public void saveToDatabase(int userId) throws SQLException {
        String sql = "INSERT INTO orders (user_id, item_name, quantity, unit_price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, item.name);
            pstmt.setDouble(3, quantityOrdered);
            pstmt.setDouble(4, item.pricePerUnit);
            
            pstmt.executeUpdate();
        }
    }

    public double getTotalPrice() {
        return quantityOrdered * item.pricePerUnit;
    }

    @Override
    public String toString() {
        return String.format("%.2f %s of %s @ Rs.%.2f = Rs.%.2f", 
            quantityOrdered, item.unit, item.name, item.pricePerUnit, getTotalPrice());
    }
}