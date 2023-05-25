package org.example.model;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.example.DB.Weather;
import org.example.DB.WeatherDAOPost;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

    /**
     @author hypec
     @version 1.0.1
     @since 04-05-2023
     */

public class WeatherBot extends TelegramLongPollingBot {

    /** Поле инициализации объекта базы данных */
    WeatherDAOPost weatherDAOImpl = new WeatherDAOPost();

        /**
         * Главная процедура, инициализирющая и регистрирующая бота
         * @param args
         */
    public static void main(String[] args) {
        WeatherBot bot = new WeatherBot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

        /**
         * Процедура обновления данных в чате
         * @param update
         */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Message inMess = update.getMessage();
            String chatId = inMess.getChatId().toString();

            if (text.equals("/start")){
                if (text.startsWith("/start")) {
                    SendMessage messageToSend = new SendMessage();
                    messageToSend.setChatId(chatId);
                    messageToSend.setText("Добро пожаловать в чат бота Погода!\nВыберите соответствующиую опцию:");

                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    keyboardMarkup.setSelective(true);
                    keyboardMarkup.setResizeKeyboard(true);
                    keyboardMarkup.setOneTimeKeyboard(true); // отключение выбора элементов(только на телефоне)
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add(new KeyboardButton("/db"));
                    keyboard.add(row);
                    row = new KeyboardRow();
                    row.add(new KeyboardButton("/help"));
                    keyboard.add(row);
                    row = new KeyboardRow();
                    row.add(new KeyboardButton("/start"));
                    keyboard.add(row);
                    keyboardMarkup.setKeyboard(keyboard);

                    messageToSend.setReplyMarkup(keyboardMarkup);

                    try {
                        execute(messageToSend);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if (text.startsWith("/weather")) {
                String location = text.substring(9);
                try {
                    String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&exclude=current&lang=ru&units=metric", location, "0dbfa25f5196a0447e750e1ac30e86ba");
                    CloseableHttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(url);
                    JSONObject response = new JSONObject(EntityUtils.toString(client.execute(request).getEntity()));
                    //System.out.println("API Response: " + response.toString()); // дебаг HTTP запроса
                    String description = response.getJSONArray("weather").getJSONObject(0).getString("description");
                    double temperature = response.getJSONObject("main").getDouble("temp");
                    String temperatureString = String.format("%.1f", temperature);

                    SendMessage messageToSend = new SendMessage();

                    messageToSend.setChatId(chatId);
                    messageToSend.setText("Погода в городе " + location + " - " + description + "\nТемпература равна:  " + temperatureString + " °C");

                    String name2 = location;
                    String description2 = description;
                    String temp2 = temperatureString;
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String time = now.format(formatter);

                    weatherDAOImpl.addWeather(new Weather(0, name2, description2, temp2, time));

                    execute(messageToSend);
                } catch (Exception e) {
                    //System.out.println("ошибка в : " + e.getMessage()); // дебаг переменной с температурой
                    SendMessage messageToSend = new SendMessage();
                    messageToSend.setChatId(chatId);
                    messageToSend.setText("Извините, информации по  местоположению "+ location + " нету!");
                    try {
                        execute(messageToSend);
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            else if (text.equals("/db")) {
                List<Weather> weathers = weatherDAOImpl.getAllWeathers();
                StringBuilder sb = new StringBuilder();
                sb.append("Погода в базе данных:\n");
                for (Weather weather : weathers) {
                    sb.append(weather.getName()).append(" - ").append(weather.getDescription()).append(", температура: ").append(weather.getTemperature()).append(" °C, время запроса: ").append(weather.getTime()).append("\n");
                }
                SendMessage messageToSend = new SendMessage();
                messageToSend.setChatId(chatId);
                messageToSend.setText(sb.toString());
                try {
                    execute(messageToSend);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (text.equals("/help")) {
                SendMessage messageToSend = new SendMessage();
                messageToSend.setChatId(chatId);
                messageToSend.setText("Для получения погоды, необходимо ввести /weather и название города\n" +
                        "для получения всей информации из базы, надо ввести /db\n"+
                        "для получения информации по средней температуре в городе из базы, необходимо ввести /dbavg и название города");
                try {
                    execute(messageToSend);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (text.startsWith("/dbavg")) {
                String city = text.substring(7);
                double averageTemperature = weatherDAOImpl.getAverageTemperatureByCity(city);
                String response = city + " - средняя температура - " + averageTemperature;

                SendMessage messageToSend = new SendMessage();
                messageToSend.setChatId(chatId);
                messageToSend.setText(response);

                try {
                    execute(messageToSend);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        }

        /**
         * Функция получения значения поля BotUsername
         * @return
         */
    @Override
    public String getBotUsername() {
        return "kursprojectbot";
    }

        /**
         * Функция получения значения поля BotToken
         * @return
         */
    @Override
    public String getBotToken() {
        return "6033289169:AAFsWsdRn2Hc-G-V5L9OewZIEzyLO7wsoP0";
    }
}