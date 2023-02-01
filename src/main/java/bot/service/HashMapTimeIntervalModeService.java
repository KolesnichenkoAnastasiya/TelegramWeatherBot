package bot.service;

import bot.entity.TimeInterval;
import java.util.HashMap;
import java.util.Map;


public class HashMapTimeIntervalModeService implements TimeIntervalModeService {
    private final Map<Long, TimeInterval> originalTimeInterval = new HashMap<>();

    public HashMapTimeIntervalModeService(){
        System.out.println("HASHMAP MODE is created");
    }

    @Override
    public TimeInterval getOriginalTimeInterval(long chatId) {
        return originalTimeInterval.getOrDefault(chatId, TimeInterval.TOMORROW);
    }

    @Override
    public void setOriginalTimeInterval(long chatId, TimeInterval timeInterval) {
        originalTimeInterval.put(chatId, timeInterval);
    }
}
