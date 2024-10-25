package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

public class ReplyCommand implements Command {
    @Override
    public void execute(Long chatId, String messageText, MyTelegramBot bot) {
        bot.sendReplyKeyboard(chatId);
    }
}
