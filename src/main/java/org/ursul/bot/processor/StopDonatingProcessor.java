package org.ursul.bot.processor;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;
import org.ursul.bot.domain.Donator;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class StopDonatingProcessor extends DefaultProcessor {

    public StopDonatingProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {

        Optional<Donator> pidar = dao.getDonator(update.getMessage().getChatId());

        List<BotApiMethod<Message>> messages = dao.getDonators().stream()
                .filter(donator -> donator.isAdmin())
                .map(admin ->
                        new SendMessage() // Create a SendMessage object with mandatory fields
                                .setChatId(admin.getChatId())
                                .enableMarkdown(true)
                                .setText(pidar.get().constructName() + " видаляється зі списку." + buildHelpMessage())
                ).collect(toList());


        messages.add(
                new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("Видалив тебе зі списку людей, які слідкують за прибиранням." + buildHelpMessage())
        );

        dao.stopDonating(update.getMessage().getChatId());

        return messages;
    }
}
