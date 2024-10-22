package telgrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

public class MyTelegramBot extends TelegramLongPollingBot {

    // Хранение состояния диалога с пользователем
    private final Map<Long, String> userStates = new HashMap<>();

    private final String BOT_TOKEN = "7295727940:AAFKg4eNiTx0yQEqOoZJtz6BsD491SEHQ24";
    private final String BOT_USERNAME = "FINANCE_BOT";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            // Получаем текущее состояние пользователя
            String currentState = userStates.getOrDefault(chatId, "START");

            switch (currentState) {
                case "START":
                    if (messageText.equals("/start")) {
                        sendFirstMessage(chatId);
                        // Переводим пользователя в состояние ожидания ответа
                        userStates.put(chatId, "WAITING_FOR_ANSWER");
                    }
                    break;

                case "WAITING_FOR_ANSWER":
                    // Пример: пользователь должен ответить на вопрос "Как вас зовут?"
                    sendMessage(chatId, "Спасибо за ответ! Мы продолжим нашу беседу.");
                    // Здесь можно добавить логику для перехода в другие состояния
                    userStates.put(chatId, "END");
                    break;

                case "END":
                    sendMessage(chatId, "Беседа завершена.");
                    break;

                default:
                    sendMessage(chatId, "Я не понимаю вашу команду.");
            }
        }
    }

    private void sendFirstMessage(Long chatId) {
        String firstMessage = "Здравствуйте! Как вас зовут?";
        sendMessage(chatId, firstMessage);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message); // Отправка сообщения
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
