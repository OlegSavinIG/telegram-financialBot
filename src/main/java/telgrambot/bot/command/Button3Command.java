package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

public class Button3Command implements Command {
    @Override
    public void execute(Long chatId, String messageText, MyTelegramBot bot) {
        bot.sendMessage(chatId, "Вы нажали на Кнопку 3.");
        bot.sendMessageToQueue(chatId, "Пользователь нажал на кнопку 3");
    }
}
