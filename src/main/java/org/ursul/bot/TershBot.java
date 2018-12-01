package org.ursul.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapdb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.ursul.bot.processor.*;

import java.util.List;

/**
 * @since 11/25/18
 */
public class TershBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TershBot.class);

    private DB db;
    private Dao dao;
    private ObjectMapper mapper;

    private String username;
    private String token;

    public TershBot(DB db, String username, String token) {
        this.db = db;
        this.dao = new Dao(this.db);
        this.username = username;
        this.token = token;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Chat chat = update.getMessage().getChat();
            String name = chat.getFirstName() + " " + chat.getLastName();
            String message = update.getMessage().getText();

            logger.info("{} replied with message: {}", name, message);
            Processor processor = getProcessor(message);

            List<BotApiMethod<Message>> messages = processor.process(update);

            try {
                for (BotApiMethod<Message> botApiMethod : messages) {
                    execute(botApiMethod);
                }
            } catch (TelegramApiException e) {
                logger.error("Error received", e);
            }
        }
    }

    private Processor getProcessor(String message) {
        Processor processor;
        switch (message) {
            case "/start":
                processor = new DefaultProcessor(dao);
                break;
            case "/help":
                processor = new DefaultProcessor(dao);
                break;
            case "/donators":
                processor = new DonatorsProcessor(dao);
                break;
            case "/becomedonator":
                processor = new BecomeDonatorProcessor(dao);
                break;
            case "/spends":
                processor = new CalculateSpendsProcessor(dao);
                break;
            case "/previousspends":
                processor = new CalculatePreviousMonthsSpendsProcessor(dao);
                break;
            case "/nextspends":
                processor = new CalculateNextMonthsSpendsProcessor(dao);
                break;
            case "/stopdonating":
                processor = new StopDonatingProcessor(dao);
                break;
            case "/removelastdonate":
                processor = new RemoveLastDonateProcessor(dao);
                break;
            case "/tartaletka":
                processor = new BecomeAdminProcessor(dao);
                break;
            case "/subscribetodonates":
                processor = new SubscribeToDonatesProcessor(dao);
                break;
            case "/unsubscribetodonates":
                processor = new SubscribeToDonatesProcessor(dao);
                break;
            default:
                processor = new FuzzyProcessor(dao);
        }

        return processor;
    }

    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onClosing() {
        super.onClosing();
    }
}
