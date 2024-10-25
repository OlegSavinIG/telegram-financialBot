package telgrambot.bot;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telgrambot.bot.command.CommandHandler;
import telgrambot.bot.keyboard.InlineKeyboard;
import telgrambot.bot.keyboard.ReplyKeyboard;

import java.util.ArrayList;
import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {
    private static final String QUEUE_NAME = "telegram_messages";
    private final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private final String BOT_USERNAME = "FinanceAndInvestmentBot";
    private final CommandHandler commandHandler = new CommandHandler();
    private final ReplyKeyboard replyKeyboard = new ReplyKeyboard();
    private final InlineKeyboard inlineKeyboard = new InlineKeyboard();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            commandHandler.handleCommand(chatId, messageText, this);
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            // Используем новый метод для обработки callback-команд
            commandHandler.handleCallback(chatId, callbackData, this);
        }
    }

    // Метод для отправки сообщения в RabbitMQ
    public void sendMessageToQueue(Long chatId, String messageText) {
        System.out.println("Попытка отправки сообщения в очередь: " + messageText);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = chatId + ";" + messageText;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");

        } catch (Exception e) {
            System.err.println("Ошибка при отправке сообщения в RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void registerCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Запустить бота"));
        commands.add(new BotCommand("/help", "Получить справку"));
        commands.add(new BotCommand("/statistic", "Показать статистику"));
        commands.add(new BotCommand("/inline", "Показать клавиатуру"));
        commands.add(new BotCommand("/reply", "Показать клавиатуру"));
        SetMyCommands setMyCommands = new SetMyCommands(commands, null, null);

        try {
            this.execute(setMyCommands);
            System.out.println("Команды успешно зарегистрированы.");
        } catch (TelegramApiException e) {
            System.err.println("Ошибка при регистрации команд: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendInlineKeyboard(Long chatId) {
        InlineKeyboard inlineKeyboard = new InlineKeyboard();
        inlineKeyboard.sendInlineKeyboard(chatId, this); // Передаем chatId и экземпляр бота
    }

    public void sendReplyKeyboard(Long chatId) {
        ReplyKeyboard replyKeyboard = new ReplyKeyboard();
        replyKeyboard.sendReplyKeyboard(chatId, this); // Передаем chatId и экземпляр бота
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
