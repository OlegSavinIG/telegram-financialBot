package telgrambot.bot.repository.redis;

import redis.clients.jedis.Jedis;

public class RedisService {

    private static final Jedis jedis = new Jedis("localhost", 6379);

    // Установка значения в Redis
    public void setUserState(Long chatId, String state) {
        jedis.set(chatId.toString(), state);
    }

    // Получение значения из Redis
    public String getUserState(Long chatId) {
        return jedis.get(chatId.toString());
    }

    // Удаление данных по завершении
    public void clearUserState(Long chatId) {
        jedis.del(chatId.toString());
    }
    public boolean existUser(Long chatId) {
        return jedis.exists(chatId.toString());
    }
}
