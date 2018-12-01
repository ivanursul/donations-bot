package org.ursul.bot.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.ursul.bot.Dao;
import org.ursul.bot.domain.Donator;
import org.ursul.bot.domain.Spend;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class FuzzyProcessor extends DefaultProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FuzzyProcessor.class);

    public static final String DONATING_PEOPLE = "Donating people ";

    public FuzzyProcessor(Dao dao) {
        super(dao);
    }

    @Override
    public List<BotApiMethod<Message>> process(Update update) {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (isPeopleProcessor(message)) {
            String number = message.replace(DONATING_PEOPLE, "");

            try {
                Integer peopleCount = Integer.parseInt(number);
                dao.donateForPeople(chatId, peopleCount);

                return Collections.singletonList(
                        new SendMessage() // Create a SendMessage object with mandatory fields
                                .setChatId(update.getMessage().getChatId())
                                .enableMarkdown(true)
                                .setText("Збільшив кількість людей" + buildHelpMessage())
                );
            } catch (Exception e) {
                return Collections.singletonList(
                        new SendMessage() // Create a SendMessage object with mandatory fields
                                .setChatId(update.getMessage().getChatId())
                                .enableMarkdown(true)
                                .setText("Шось пішло не так" + buildHelpMessage())
                );
            }
        } else if (isDonatingMoney(message)) {
            try {
                Integer amount = Integer.parseInt(message.substring(0, message.indexOf(' ')));
                String spendMessage = message.substring(message.indexOf(' ') + 1);

                boolean plusMonth = spendMessage.contains("+1");
                boolean minusMonth = spendMessage.contains("-1");

                if (!dao.isDonator(chatId)) {
                    Chat chat = update.getMessage().getChat();
                    dao.becomeDonator(new Donator(
                            chat.getUserName(), chat.getId(), chat.getFirstName(), chat.getLastName(), 1, false
                    ));
                }

                LocalDateTime localDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()
                );

                if (plusMonth) {
                    localDateTime = localDateTime.plusMonths(1);
                    spendMessage = spendMessage.replace("+1", "");
                }

                if (minusMonth) {
                    localDateTime = localDateTime.minusMonths(1);
                    spendMessage = spendMessage.replace("-1", "");
                }

                long millis = localDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();

                Spend spend = new Spend(
                        chatId,
                        millis,
                        amount,
                        spendMessage
                );

                dao.spend(
                        spend
                );

                Donator donator = dao.getDonator(chatId).get();

                List<BotApiMethod<Message>> messages = dao.getDonationSubscribers().stream()
                        .map(ds -> dao.getDonator(ds.getChatId()).get())
                        .filter(d -> d != null)
                        .map(d -> new SendMessage()
                                .setChatId(d.getChatId())
                                .enableMarkdown(true)
                                .setText(
                                        donator.constructName() + ": " + spend.getMessage() + spend.getAmount() + buildHelpMessage())
                        ).collect(toList());

                messages.add(
                        new SendMessage()
                                .setChatId(update.getMessage().getChatId())
                                .enableMarkdown(true)
                                .setText("Заєбішчє..." + buildHelpMessage())

                );

                return messages;
            } catch (Exception e) {
                logger.error("Something went wrong", e);
                return Collections.singletonList(
                        new SendMessage() // Create a SendMessage object with mandatory fields
                                .setChatId(update.getMessage().getChatId())
                                .enableMarkdown(true)
                                .setText("Шось пішло не так" + buildHelpMessage())
                );
            }
        }

        return super.process(update);
    }

    private boolean isDonatingMoney(String message) {
        String[] words = message.split(" ");

        if (words.length < 2 || !(StringUtils.isNumeric(words[0]) || NumberUtils.isParsable(words[0]))) {
            return false;
        }

        return true;
    }

    public boolean isPeopleProcessor(String message) {
        return message.contains(DONATING_PEOPLE);
    }
}
