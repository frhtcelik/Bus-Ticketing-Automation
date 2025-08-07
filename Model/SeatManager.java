package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SeatManager {
    private static Map<Integer, String> seatStatusCache = new HashMap<>();
    
    public static void initializeSeatStatus() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT KoltukNo, tc FROM yolcu_bilgileri";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String koltukNo = rs.getString("KoltukNo");
                String tc = rs.getString("tc");
                int seatNumber = Integer.parseInt(koltukNo);
                
                // TC numarasının son hanesi çift ise kadın, tek ise erkek
                int lastDigit = Character.getNumericValue(tc.charAt(tc.length() - 1));
                String gender = (lastDigit % 2 == 0) ? "female" : "male";
                
                seatStatusCache.put(seatNumber, gender);
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static String getSeatStatus(int seatNumber) {
        // Önce cache'den kontrol et
        if (seatStatusCache.containsKey(seatNumber)) {
            return seatStatusCache.get(seatNumber);
        }
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT tc FROM yolcu_bilgileri WHERE KoltukNo = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, String.valueOf(seatNumber));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String tc = rs.getString("tc");
                // TC numarasının son hanesi çift ise kadın, tek ise erkek
                int lastDigit = Character.getNumericValue(tc.charAt(tc.length() - 1));
                String gender = (lastDigit % 2 == 0) ? "female" : "male";
                
                // Cache'e ekle
                seatStatusCache.put(seatNumber, gender);
                
                rs.close();
                pstmt.close();
                conn.close();
                
                return gender;
            }
            
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return "empty";
    }
    
    public static void updateSeatStatus(int seatNumber, String gender) {
        seatStatusCache.put(seatNumber, gender);
    }
} 