package telgrambot.bot.keyboard;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telgrambot.bot.MyTelegramBot;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboard {

    public void sendInlineKeyboard(Long chatId, MyTelegramBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите опцию:");

        // Создаем Inline-клавиатуру
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Первая строка кнопок
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Кнопка 1");
        button1.setCallbackData("button1");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Кнопка 2");
        button2.setCallbackData("button2");

        // Добавляем кнопки в первую строку
        rowInline.add(button1);
        rowInline.add(button2);

        // Вторая строка кнопок
        List<InlineKeyboardButton> secondRowInline = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Кнопка 3");
        button3.setCallbackData("button3");

        // Добавляем кнопку во вторую строку
        secondRowInline.add(button3);

        // Добавляем строки с кнопками в клавиатуру
        rowsInline.add(rowInline);
        rowsInline.add(secondRowInline);

        // Устанавливаем клавиатуру в сообщение
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            bot.execute(message); // Отправляем сообщение с клавиатурой через экземпляр бота
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
