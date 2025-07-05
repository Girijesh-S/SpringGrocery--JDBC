import java.sql.*;

public class UserDetails {
    public int id;
    public String name;
    public String phone;
    public String altPhone;
    public String pincode;
    public String city;
    public String state;
    public String addressLine1;
    public String addressLine2;
    public String deliveryDate;

    public void saveToDatabase() throws SQLException {
        String sql = "INSERT INTO users (name, phone, alt_phone, pincode, city, state, address_line1, address_line2, delivery_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, altPhone);
            pstmt.setString(4, pincode);
            pstmt.setString(5, city);
            pstmt.setString(6, state);
            pstmt.setString(7, addressLine1);
            pstmt.setString(8, addressLine2);
            pstmt.setString(9, deliveryDate);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Name: %s\nPhone: %s\nAlt Phone: %s\nAddress: %s, %s, %s, %s\nDelivery Date: %s",
            name, phone, altPhone, addressLine1, addressLine2, city, state, deliveryDate
        );
    }
}