package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, Command> callbackCommands = new HashMap<>();

    public CommandHandler() {
        commands.put("/start", new StartCommand());
        commands.put("/help", new HelpCommand());
        commands.put("/statistic", new StatisticCommand());
        commands.put("/reply", new ReplyCommand());
        commands.put("/inline", new InlineCommand());

        // Добавляем обработку callback-команд
        callbackCommands.put("button1", new Button1Command());
        callbackCommands.put("button2", new Button2Command());
        callbackCommands.put("button3", new Button3Command());
    }

    public void handleCommand(Long chatId, String messageText, MyTelegramBot bot) {
        Command command = commands.get(messageText.split(" ")[0]);
        if (command != null) {
            command.execute(chatId, messageText, bot);
        } else {
            bot.sendMessage(chatId, "Извините, я не понимаю вашу команду.");
        }
    }

    public void handleCallback(Long chatId, String callbackData, MyTelegramBot bot) {
        Command callbackCommand = callbackCommands.get(callbackData);
        if (callbackCommand != null) {
            callbackCommand.execute(chatId, callbackData, bot);
        } else {
            bot.sendMessage(chatId, "Неизвестная команда кнопки.");
        }
    }
}
