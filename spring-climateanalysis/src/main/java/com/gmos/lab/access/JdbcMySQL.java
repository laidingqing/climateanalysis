package com.gmos.lab.access;

import com.gmos.lab.model.Station;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

/******************************************************************************
 * MySQL Server
 * URL: jdbc:mysql://localhost:3306/test
 */
public class JdbcMySQL {
    private static String driverName = "com.mysql.jdbc.Driver";

    public static Station query(String id) {
        Station ret = new Station();
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return ret;
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "87552790");
            stmt = con.createStatement();
            String sql = "select * from station where glatitude is not null and glongitude is not null and gstation='" + id + "'";
            res = stmt.executeQuery(sql);
            if (res.next()) {
                String station = res.getString(1);
                double latitude = res.getDouble(2);
                double longitude = res.getDouble(3);
                ret.setId(station);
                ret.setLatitude(latitude);
                ret.setLongitude(longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (res != null) res.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            }catch(Exception ex){}
        }

        return ret;
    }

}
