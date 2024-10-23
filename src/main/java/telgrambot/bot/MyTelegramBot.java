package telgrambot.bot;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telgrambot.bot.command.CommandHandler;

public class MyTelegramBot extends TelegramLongPollingBot {
    private static final String QUEUE_NAME = "telegram_messages";
    private final CommandHandler commandHandler = new CommandHandler();
    private final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private final String BOT_USERNAME = "FINANCE_BOT";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            commandHandler.handleCommand(chatId, messageText, this);
        }
    }
    // Метод для отправки сообщения в RabbitMQ
    public void sendMessageToQueue(Long chatId, String messageText) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // Настройте хост RabbitMQ

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // Создаем сообщение для очереди
            String message = chatId + ";" + messageText;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Отправка сообщений
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
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
}
