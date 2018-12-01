package org.ursul.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;
import org.ursul.bot.domain.DonationSubscriber;

import java.util.Collections;
import java.util.List;

public class SubscribeToDonatesProcessor extends DefaultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SubscribeToDonatesProcessor.class);

    public SubscribeToDonatesProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        Long chatId = update.getMessage().getChatId();

        if (!dao.isSubscribedToDonationNews(chatId)) {
            dao.subscribe(new DonationSubscriber(chatId));
        } else {
            dao.unsubsribeFromDonationSubscriptio(chatId);
        }

        return Collections.singletonList(
                new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("Готово!!!" + buildHelpMessage())
        );
    }
}
