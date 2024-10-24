package telgrambot.bot.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageConsumer {

    private static final String QUEUE_NAME = "telegram_messages";
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        System.out.println("Попытка подключения к RabbitMQ Consumer на localhost...");
        Connection connection = factory.newConnection();
        System.out.println("Успешное подключение к RabbitMQ.");

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");

            // Разделим сообщение на chatId и текст
            String[] parts = message.split(";");
            Long chatId = Long.parseLong(parts[0]);
            String messageText = parts[1];

            // Обрабатываем сообщение и отправляем ответ через Telegram API
            processMessage(chatId, messageText);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    // Обработка сообщения и отправка ответа пользователю
    private static void processMessage(Long chatId, String messageText) {
        // Создаем экземпляр бота для отправки сообщений
        DefaultAbsSender bot = new DefaultAbsSender(new DefaultBotOptions()) {
            @Override
            public String getBotToken() {
                return BOT_TOKEN;
            }
        };

        // Логика обработки сообщения (например, отвечаем на команду)
        String response = "Вы отправили: " + messageText;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(response);

        try {
            bot.execute(sendMessage); // Отправляем сообщение пользователю
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
