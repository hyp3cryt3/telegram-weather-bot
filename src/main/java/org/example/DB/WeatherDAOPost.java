package org.example.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WeatherDAOPost implements WeatherDAO {
    private Connection conn;

    public WeatherDAOPost() {

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/weather", "postgres", "Nd31012001");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<Weather> getAllWeathers() {
        List<Weather> tasks = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM weather");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Weather task = new Weather(rs.getInt("id"),
                        //rs.getInt("id");
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("temp"),
                        rs.getString("time"));
                tasks.add(task);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public Weather getWeatherById(int id) {
        Weather task = null;
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM weather WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Weather weather = new Weather(rs.getInt("id"),
                        //rs.getInt("id");
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("temp"),
                        rs.getString("time"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public void addWeather(Weather weather) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO weather (name, description, temp, time) VALUES (?, ?, ?, ?)");
            ps.setString(1, weather.getName());
            ps.setString(2, weather.getDescription());
            ps.setString(3, weather.getTemperature());
            ps.setString(4,weather.getTime());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void updateWeather(Weather weather) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE weather SET name = ?, description = ?, temp = ?, time = ? WHERE id = ?");
            ps.setString(1, weather.getName());
            ps.setString(2, weather.getDescription());
            ps.setString(3, weather.getTemperature());
            ps.setString(4,weather.getTime());
            ps.setInt(5, weather.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteWeather(int id) {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM weather WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getAverageTemperatureByCity(String city) {
        String sql = "SELECT AVG(REPLACE(temp, ',', '.')::double precision) AS avg_temp FROM weather WHERE name=?\n";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, city);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_temp");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // или другое значение по умолчанию, если не удалось получить среднее значение
    }


}
