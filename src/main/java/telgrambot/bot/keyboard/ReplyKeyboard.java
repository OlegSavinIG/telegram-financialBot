package telgrambot.bot.keyboard;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telgrambot.bot.MyTelegramBot;

import java.util.ArrayList;
import java.util.List;

public class ReplyKeyboard {

    public void sendReplyKeyboard(Long chatId, MyTelegramBot bot) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите опцию:");

        // Создаем Reply-клавиатуру
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true); // Устанавливаем параметр для автоматического изменения размера клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строка кнопок
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Команда 1"));
        row.add(new KeyboardButton("Команда 2"));

        // Вторая строка кнопок
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton("Команда 3"));

        // Добавляем строки с кнопками в клавиатуру
        keyboard.add(row);
        keyboard.add(secondRow);
        keyboardMarkup.setKeyboard(keyboard);

        // Устанавливаем клавиатуру в сообщение
        message.setReplyMarkup(keyboardMarkup);

        try {
            bot.execute(message); // Отправляем сообщение с клавиатурой через экземпляр бота
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
