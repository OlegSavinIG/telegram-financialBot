package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

public class StartCommand implements Command {
    @Override
    public void execute(Long chatId, String messageText, MyTelegramBot bot) {
        String welcomeMessage = "Здравствуйте! Добро пожаловать в наш сервис.";
        bot.sendMessageToQueue(chatId, welcomeMessage);
    }
}
