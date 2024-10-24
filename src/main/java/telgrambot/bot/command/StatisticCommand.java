package telgrambot.bot.command;

import telgrambot.bot.MyTelegramBot;
import telgrambot.bot.financeapi.StatisticServiceApi;

public class StatisticCommand implements Command {
    private final StatisticServiceApi statisticService;

    public StatisticCommand() {
        this.statisticService = new StatisticServiceApi();
    }

    @Override
    public void execute(Long chatId, String messageText, MyTelegramBot bot) {
        String vacancies = statisticService.getStatistics();
        bot.sendMessageToQueue(chatId, "Статистика: \n" + vacancies);
    }
}