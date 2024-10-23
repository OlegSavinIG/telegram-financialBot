package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

public interface Command {
    void execute(Long chatId, String messageText, MyTelegramBot bot);
}
