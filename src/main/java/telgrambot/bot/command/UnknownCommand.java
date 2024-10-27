package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

public class UnknownCommand implements Command {
    @Override
    public void execute(Long chatId, String identifier, MyTelegramBot bot) {
        bot.sendMessage(chatId, "Извините, я не понимаю вашу команду.");
    }
}
