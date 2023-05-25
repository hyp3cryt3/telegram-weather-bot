package org.example.DB;

import java.util.List;

public interface WeatherDAO {
    void addWeather(Weather weather);
    void updateWeather(Weather weather);
    void deleteWeather(int id);
    List<Weather> getAllWeathers();
    Weather getWeatherById(int id);
}
