package org.ursul.bot;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @since 11/25/18
 */
public class Main {

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        String username = args[0];
        String token = args[1];

        String path = args.length < 3 ?
                "/Users/ivanursul/Dropbox/file.db" :
                args[2];

        DB db = DBMaker
                .fileDB(path)
                .closeOnJvmShutdown()
                .make();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            db.close();
        }));

        try {
            botsApi.registerBot(new TershBot(db, username, token));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}