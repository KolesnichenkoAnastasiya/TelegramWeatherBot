package bot.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;

@Getter
@Setter
@AllArgsConstructor
public class weatherDayForecast {
    int day;
    DayOfWeek weekDay;
    int tempMin;
    int tempMax;

    public String toString(){
        String weekDayRus = weekDay.toString();
        switch (weekDay) {
        case FRIDAY: weekDayRus = " Пт.\n";
            break;
        case SATURDAY: weekDayRus = " Сб.\n";
            break;
        case SUNDAY: weekDayRus = " Вс.\n";
            break;
        case MONDAY: weekDayRus = " Пн.\n";
            break;
        case TUESDAY:weekDayRus = " Вт.\n";
            break;
        case WEDNESDAY:weekDayRus = " Ср.\n";
            break;
        case THURSDAY:weekDayRus = " Чт.\n";
            break;}

        return day + " " + weekDayRus +  "Днём до: " +
                tempMax + "°C\nНочью до: " + tempMin + "°C\n\n";
    }
}
