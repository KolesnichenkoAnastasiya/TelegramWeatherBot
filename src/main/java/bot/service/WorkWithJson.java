package bot.service;

import bot.weather.weatherDayForecast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class WorkWithJson {
    public static weatherDayForecast[] weatherAllDayList(String response){
        ArrayList <weatherDayForecast> weatherDayList = new ArrayList<>();
        JSONObject jo = new JSONObject(response);
        JSONArray temp = jo.getJSONArray("list");
        Iterator tempItr = temp.iterator();
        while (tempItr.hasNext()) {
            try {
                JSONObject test = (JSONObject) tempItr.next();
                LocalDate localDate = LocalDate.parse((test.get("dt_txt").toString()).substring(0,10));
                int day = localDate.getDayOfMonth();
                DayOfWeek dayOfWeek = localDate.getDayOfWeek();
                Integer minIn = tempFormatter(test.getJSONObject("main").getDouble("temp_min"));
                Integer maxIn = tempFormatter(test.getJSONObject("main").getDouble("temp_max"));
                weatherDayList.add(new weatherDayForecast(day, dayOfWeek, minIn, maxIn));
            } catch (Exception e) {
                System.out.println("Ошибка при извлечении данных из json!");
            }
        }
        return weatherFormatter(weatherDayList);
    }

    public static weatherDayForecast[] weatherFormatter (ArrayList <weatherDayForecast> weatherDayList) {
        weatherDayForecast [] weatherDayOut = new weatherDayForecast[weatherDayList.size()];
        int indexOut = 0;
        int tempMin;
        int tempMax;
        weatherDayOut[0] = weatherDayList.get(0);
        Iterator itr = weatherDayList.iterator();
        while (itr.hasNext()) {
            weatherDayForecast nowItr = (weatherDayForecast) itr.next();
            if (nowItr.getDay() == weatherDayOut[indexOut].getDay()) {
                if (nowItr.getTempMin() < weatherDayOut[indexOut].getTempMin()) {
                    tempMin = nowItr.getTempMin();
                } else {
                    tempMin = weatherDayOut[indexOut].getTempMin();
                }
                if (nowItr.getTempMax() > weatherDayOut[indexOut].getTempMax()) {
                    tempMax = nowItr.getTempMax();
                } else {
                    tempMax = weatherDayOut[indexOut].getTempMax();
                }
                weatherDayOut[indexOut] = new weatherDayForecast(nowItr.getDay(),
                        nowItr.getWeekDay(), tempMin, tempMax);
            } else if (nowItr.getDay()!=weatherDayOut[indexOut].getDay()) {
                weatherDayOut[indexOut + 1] = nowItr;
                indexOut++;
            }
        }
        return weatherDayOut;
    }

    public static Integer tempFormatter(Double tempt) {
        return (int)Math.ceil(tempt-273.15);
    }
}
