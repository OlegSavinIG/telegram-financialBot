package telgrambot.bot.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageConsumer {
    private static final String QUEUE_NAME = "telegram_messages";
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final Logger logger = Logger.getLogger(MessageConsumer.class.getName());

    private final DefaultAbsSender bot;
    private Connection connection;
    private Channel channel;

    public MessageConsumer() {
        this.bot = createBot();
    }

    public static void main(String[] args) {
        MessageConsumer consumer = new MessageConsumer();
        consumer.startListening();
    }

    private DefaultAbsSender createBot() {
        return new DefaultAbsSender(new DefaultBotOptions()) {
            @Override
            public String getBotToken() {
                return BOT_TOKEN;
            }
        };
    }

    private void startListening() {
        try {
            connectToRabbitMQ();
            listenForMessages();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при подключении к RabbitMQ или Telegram: ", e);
            closeResources();
        }
    }

    private void connectToRabbitMQ() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        logger.info("Попытка подключения к RabbitMQ Consumer на localhost...");

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        logger.info("Успешное подключение к RabbitMQ.");
        logger.info(" [*] Ожидание сообщений. Нажмите CTRL+C для выхода.");
    }

    private void listenForMessages() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            logger.info(" [x] Получено сообщение: '" + message + "'");

            String[] parts = message.split(";");
            if (parts.length == 2) {
                try {
                    Long chatId = Long.parseLong(parts[0]);
                    String messageText = parts[1];
                    processMessage(chatId, messageText);
                } catch (NumberFormatException e) {
                    logger.warning("Ошибка разбора chatId: " + parts[0]);
                }
            } else {
                logger.warning("Сообщение не соответствует ожидаемому формату: " + message);
            }
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    private void processMessage(Long chatId, String messageText) {
        String response = "Вы отправили: " + messageText;
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(response)
                .build();

        try {
            bot.execute(sendMessage);
            logger.info("Сообщение отправлено в Telegram: " + response);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Ошибка при отправке сообщения в Telegram: ", e);
        }
    }

    private void closeResources() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            logger.info("Соединение с RabbitMQ закрыто.");
        } catch (IOException | TimeoutException e) {
            logger.log(Level.SEVERE, "Ошибка при закрытии соединения с RabbitMQ: ", e);
        }
    }
}
