package telgrambot.bot.state;

import java.util.HashMap;
import java.util.Map;

public class UserState {
    private final Map<Long, String> userStates = new HashMap<>();

    public String getState(Long chatId) {
        return userStates.getOrDefault(chatId, "START");
    }

    public void setState(Long chatId, String state) {
        userStates.put(chatId, state);
    }
}
