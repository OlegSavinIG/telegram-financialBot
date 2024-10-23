package telgrambot.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public class FileSender extends TelegramLongPollingBot {
    // Метод для отправки файла
    private void sendDocument(Long chatId, File file) {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId.toString());
        sendDocumentRequest.setDocument(new InputFile(file));

        try {
            execute(sendDocumentRequest); // Отправка файла
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Пример использования метода отправки файла
    private void sendFileExample(Long chatId) {
        File resume = new File("path/to/file.pdf"); // Укажите путь к вашему файлу
        sendDocument(chatId, resume);
    }
    private void sendLink(Long chatId) {
        String messageWithLink = "Вот ссылка на наш сайт: [Нажмите здесь](https://example.com)";
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageWithLink);
        message.enableMarkdown(true); // Включаем поддержку Markdown для ссылки

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
