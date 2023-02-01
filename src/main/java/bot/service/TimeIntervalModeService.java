package bot.service;

import bot.entity.TimeInterval;

public interface TimeIntervalModeService {
    static TimeIntervalModeService getInstance(){
        return new HashMapTimeIntervalModeService();
    }
    TimeInterval getOriginalTimeInterval(long chatId);
    void setOriginalTimeInterval (long chatId, TimeInterval timeInterval);
}
