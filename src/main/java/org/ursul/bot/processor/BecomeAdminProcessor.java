package org.ursul.bot.processor;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;

import java.util.Collections;
import java.util.List;

public class BecomeAdminProcessor extends DefaultProcessor {
    public BecomeAdminProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {

        dao.becomeAdmin(update.getMessage().getChatId());

        return Collections.singletonList(
                new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("Ти Адмін, йоу" + buildHelpMessage())
        );
    }
}
