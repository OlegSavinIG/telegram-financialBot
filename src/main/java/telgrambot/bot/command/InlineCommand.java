package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;

public class InlineCommand implements Command {
    @Override
    public void execute(Long chatId, String messageText, MyTelegramBot bot) {
        bot.sendInlineKeyboard(chatId);
    }
}
