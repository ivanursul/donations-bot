package org.ursul.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;
import org.ursul.bot.domain.Donator;

import java.util.Collections;
import java.util.List;

public class BecomeDonatorProcessor extends DefaultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BecomeDonatorProcessor.class);

    public BecomeDonatorProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        Message message = update.getMessage();
        Chat chat = message.getChat();

        logger.info("Creting a new subscriber");
        Donator contact = new Donator(
                chat.getUserName(), chat.getId(), chat.getFirstName(), chat.getLastName(), 1, false
        );

        dao.becomeDonator(contact);

        return Collections.singletonList(
                new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("Добавив тебе в список людей, які скидуються на офіс." + buildHelpMessage())
        );
    }
}
