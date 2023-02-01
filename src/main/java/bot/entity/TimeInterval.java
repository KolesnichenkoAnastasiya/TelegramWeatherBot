package bot.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeInterval {
    NOW("Сегодня", 1),
    TOMORROW ("Завтра", 2),
    THREE_DAYS ("3 дня", 3),
    SIX_DAYS ("6 дней", 5);
    private final String text;
    private final int day;

    @Override
    public String toString() {
        return text;
    }
    public static TimeInterval timeIntervalByText (String text) {
        switch (text){
            case "Сегодня": return TimeInterval.NOW;
            case "Завтра": return TimeInterval.TOMORROW;
            case "3 дня": return TimeInterval.THREE_DAYS;
            case "6 дней": return TimeInterval.SIX_DAYS;
            default: return null;
        }
    }
}
