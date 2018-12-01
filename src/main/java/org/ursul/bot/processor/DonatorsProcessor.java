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
import java.util.Objects;
import java.util.stream.Collectors;

public class DonatorsProcessor extends DefaultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DonatorsProcessor.class);

    public DonatorsProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        String message = dao.getDonators().stream()
                .filter(donator -> Objects.nonNull(donator))
                .map(donator -> donator.constructName())
                .collect(Collectors.joining("\n"));

        logger.info("Donators: {}", message);

        return Collections.singletonList(
                new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText(message + buildHelpMessage())
        );
    }
}
