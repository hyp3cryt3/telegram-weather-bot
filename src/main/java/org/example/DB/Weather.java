package org.example.DB;


public class Weather {
    /** поле Id*/
    private int id;
    /** Поле name*/
    private String name;
    /** Поле description*/
    private String description;
    /** Поле температура*/
    private String temperature;
    /** Поле time*/
    private String time;

    /** Конструктор создания нового объекта с определёнными значениями
     *
     * @param id - id
     * @param name - название
     * @param description - состояние
     * @param temperature - температура
     * @param time - время
     */
    public Weather(int id, String name, String description, String temperature, String time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.temperature = temperature;
        this.time = time;
    }

// getters and setters

    /** Функция получения значения поля {@link Weather#id}*/
    public int getId() {
        return id;
    }

    /** Функция вставки значения поля {@link Weather#id}*/
    public void setId(int id) {
        this.id = id;
    }

    /** Функция получения значения поля {@link Weather#name}*/
    public String getName() {
        return name;
    }

    /** Функция вставки значения поля {@link Weather#name}*/
    public void setName(String name) {
        this.name = name;
    }

    /** Функция получения значения поля {@link Weather#description}*/
    public String getDescription() {
        return description;
    }

    /** Функция вставки значения поля {@link Weather#description}*/
    public void setDescription(String description) {
        this.description = description;
    }

    /** Функция получения значения поля {@link Weather#temperature}*/
    public String getTemperature() {
        return temperature;
    }

    /** Функция вставки значения поля {@link Weather#temperature}*/
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    /** Функция получения значения поля {@link Weather#time}*/
    public String getTime() {
        return time;
    }

    /** Функция вставки значения поля {@link Weather#time}*/
    public void setTime(String time) {
        this.time = time;
    }
}