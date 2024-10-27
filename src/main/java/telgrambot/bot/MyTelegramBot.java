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
import telgrambot.bot.command.Command;
import telgrambot.bot.command.CommandHandler;
import telgrambot.bot.command.*;
import telgrambot.bot.command.StartCommand;
import telgrambot.bot.keyboard.InlineKeyboard;
import telgrambot.bot.keyboard.ReplyKeyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MyTelegramBot extends TelegramLongPollingBot {
    private static final String QUEUE_NAME = "telegram_messages";
    private static final Logger logger = Logger.getLogger(MyTelegramBot.class.getName());
    private final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private final String BOT_USERNAME = "FinanceAndInvestmentBot";
    private final CommandHandler commandHandler;
    private final ReplyKeyboard replyKeyboard;
    private final InlineKeyboard inlineKeyboard;
    private Connection connection;
    private Channel channel;

    public MyTelegramBot() {
        Map<String, Command> commands = Map.of(
                "/start", new StartCommand(),
                "/help", new HelpCommand(),
                "/statistic", new StatisticCommand(),
                "/reply", new ReplyCommand(),
                "/inline", new InlineCommand(),
                "button1", new Button1Command(),
                "button2", new Button2Command(),
                "button3", new Button3Command()
        );
        this.commandHandler = new CommandHandler(commands);
        this.replyKeyboard = new ReplyKeyboard();
        this.inlineKeyboard = new InlineKeyboard();
        setupRabbitMQ();
        registerCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            processTextMessage(update);
        } else if (update.hasCallbackQuery()) {
            processCallbackQuery(update);
        }
    }

    public void sendMessageToQueue(Long chatId, String messageText) {
        logger.info("Отправка сообщения в очередь: " + messageText);
        try {
            String message = chatId + ";" + messageText;
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            logger.info("Сообщение отправлено: " + message);
        } catch (Exception e) {
            logger.severe("Ошибка при отправке в RabbitMQ: " + e.getMessage());
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
            logger.info("Команды успешно зарегистрированы.");
        } catch (TelegramApiException e) {
            logger.severe("Ошибка при регистрации команд: " + e.getMessage());
        }
    }

    public void sendInlineKeyboard(Long chatId) {
        inlineKeyboard.sendInlineKeyboard(chatId, this);
    }

    public void sendReplyKeyboard(Long chatId) {
        replyKeyboard.sendReplyKeyboard(chatId, this);
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.severe("Ошибка при отправке сообщения: " + e.getMessage());
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
        } catch (Exception e) {
            logger.warning("Ошибка при закрытии ресурсов RabbitMQ: " + e.getMessage());
        }
    }
    private void processTextMessage(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        commandHandler.handleCommand(chatId, messageText, this);
    }
    private void processCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        commandHandler.handleCallback(chatId, callbackData, this);
    }
    private void setupRabbitMQ() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (Exception e) {
            logger.severe("Ошибка при подключении к RabbitMQ: " + e.getMessage());
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
