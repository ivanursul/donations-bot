package org.ursul.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;
import org.ursul.bot.service.SpendsService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

public class CalculatePreviousMonthsSpendsProcessor extends DefaultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CalculatePreviousMonthsSpendsProcessor.class);

    private SpendsService spendsService;

    public CalculatePreviousMonthsSpendsProcessor(Dao dao) {
        super(dao);
        this.spendsService = new SpendsService(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault());

        long startOfPreiousMonth = localDateTime
                .minusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth())
                .withHour(0).withMinute(0).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli();

        long endOfPreviousMonth = localDateTime
                .minusMonths(1)
                .with(lastDayOfMonth())
                .withHour(0).withMinute(0).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli();

        String previousMonth = spendsService.getSpendMessage(startOfPreiousMonth, endOfPreviousMonth, update.getMessage().getChatId());

        return Collections.singletonList(
                new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText(previousMonth + buildHelpMessage())
        );
    }

}
