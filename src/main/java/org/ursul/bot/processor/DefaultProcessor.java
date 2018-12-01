package org.ursul.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;

import java.util.Collections;
import java.util.List;

public class DefaultProcessor implements Processor {
    private final Logger logger = LoggerFactory.getLogger(DefaultProcessor.class);

    protected Dao dao;

    public DefaultProcessor(Dao dao) {
        this.dao = dao;
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        String message = buildDefaultMessage(update.getMessage().getChatId());

        return Collections.singletonList(
                new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText(message)
        );
    }

    protected String buildHelpMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n\n");
        builder.append("/help - Допомога\n");

        return builder.toString();
    }

    protected String buildDefaultMessage(Long chatId) {
        StringBuilder builder = new StringBuilder();

        builder.append("Привіт, ти говориш з ботом, який слідкує за тим, шоб всі були чемні!\n");
        builder.append("\n");

        builder.append("*Інформаційні команди* \n");
        builder.append("/donators - Список людей, які скидуються\n");
        builder.append("/spends - Кому за шо скидатись\n");
        builder.append("/previousspends - Кому за шо скидатись минулого місяця\n");
        builder.append("/nextspends - Кому за шо скидатись наступного місяця\n");

        builder.append("\n*Дії* \n");
        if (dao.isDonator(chatId)) {
            builder.append("/stopdonating - Я не скидуюсь грошима, пішли нахуй\n");
        } else {
            builder.append("/becomedonator - Я скидуюсь грошима\n");
        }
        builder.append("/removelastdonate - Видалити останню витрату\n");
        builder.append("/help - Допомога\n");

        if (dao.isSubscribedToDonationNews(chatId)) {
            builder.append("/unsubscribetodonates - Відписатись від витрат\n");
        } else {
            builder.append("/subscribetodonates - Підписатись на новини про витрати\n");
        }

        builder.append("\n*Приклади команд* \n");
        builder.append("'100 Кава' - Добавити в витрати 100 грн\n");
        builder.append("'150 Інтернет +1' - Добавити в витрати 100 грн за інтернет на наступний місяць\n");

        if (dao.isAdmin(chatId)) {
            builder.append("\n");
            builder.append("*Коли не закинули філки*\n");
            builder.append("/notify - Нагадати всім хто не скинувся\n");
        }

        return builder.toString();
    }

}
