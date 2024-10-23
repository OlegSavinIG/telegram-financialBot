package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler() {
        commands.put("/start", new StartCommand());
        commands.put("/help", new HelpCommand());
        commands.put("/statistic", new StatisticCommand());
    }

    public void handleCommand(Long chatId, String messageText, MyTelegramBot bot) {
        Command command = commands.get(messageText.split(" ")[0]);
        if (command != null) {
            command.execute(chatId, messageText, bot);
        } else {
            bot.sendMessage(chatId, "Извините, я не понимаю вашу команду.");
        }
    }
}
