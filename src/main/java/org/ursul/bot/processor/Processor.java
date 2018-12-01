package org.ursul.bot.processor;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface Processor {

    List<BotApiMethod<Message>> process(Update update);


}
