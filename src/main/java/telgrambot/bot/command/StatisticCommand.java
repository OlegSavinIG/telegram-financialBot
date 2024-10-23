package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;
import telgrambot.bot.financeapi.StatisticService;

public class StatisticCommand implements Command {
    private final StatisticService statisticService;

    public StatisticCommand() {
        this.statisticService = new StatisticService();
    }

    @Override
    public void execute(Long chatId, String messageText, MyTelegramBot bot) {
        String vacancies = statisticService.getStatistics();
        bot.sendMessageToQueue(chatId, "Статистика: \n" + vacancies);
    }
}