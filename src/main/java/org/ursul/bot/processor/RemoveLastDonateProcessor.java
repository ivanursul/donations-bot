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

public class RemoveLastDonateProcessor extends DefaultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RemoveLastDonateProcessor.class);

    public RemoveLastDonateProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        dao.getSpends(update.getMessage().getChatId()).stream()
                .sorted((s1, s2) -> Long.compare(s1.getTime(), s2.getTime()) * -1)
                .limit(1)
                .findFirst()
                .ifPresent(spend -> {
                    dao.removeSpend(spend);
                });

        return Collections.singletonList(
                new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("Готово" + buildHelpMessage())
        );
    }
}
