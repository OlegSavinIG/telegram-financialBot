package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandHandler(Map<String, Command> initialCommands) {
        this.commands.putAll(initialCommands);
    }

    public void handle(Long chatId, String identifier, MyTelegramBot bot) {
        Command command = commands.getOrDefault(identifier, new UnknownCommand());
        command.execute(chatId, identifier, bot);
    }

    public void handleCommand(Long chatId, String messageText, MyTelegramBot bot) {
        String commandText = messageText.split(" ")[0];
        handle(chatId, commandText, bot);
    }

    public void handleCallback(Long chatId, String callbackData, MyTelegramBot bot) {
        handle(chatId, callbackData, bot);
    }
}
