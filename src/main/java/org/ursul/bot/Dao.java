package org.ursul.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ursul.bot.domain.DonationSubscriber;
import org.ursul.bot.domain.Donator;
import org.ursul.bot.domain.Spend;

import java.io.IOException;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Dao {
    private static final Logger logger = LoggerFactory.getLogger(Dao.class);

    private DB db;
    private NavigableSet<String> donators;
    private NavigableSet<String> spends;
    private NavigableSet<String> donationSubscribers;
    private ObjectMapper mapper;

    public Dao(DB db) {
        this.db = db;

        this.donators = db.treeSet("donators")
                .serializer(Serializer.STRING)
                .createOrOpen();

        this.spends = db.treeSet("spends")
                .serializer(Serializer.STRING)
                .createOrOpen();

        this.donationSubscribers = db.treeSet("donationSubscribers")
                .serializer(Serializer.STRING)
                .createOrOpen();

        this.mapper = new ObjectMapper();
    }

    public List<Spend> getSpends(Long chatId) {
        return spends.stream()
                .map(s -> map(s, Spend.class))
                .filter(s -> s.getChatId().equals(chatId))
                .collect(Collectors.toList());
    }

    public void spend(Spend spend) {
        try {
            spends.add(
                    mapper.writeValueAsString(spend)
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        db.commit();
    }

    public List<DonationSubscriber> getDonationSubscribers() {
        return donationSubscribers.stream()
                .map(ds -> map(ds, DonationSubscriber.class))
                .collect(Collectors.toList());
    }

    public boolean isSubscribedToDonationNews(Long chatId) {
        return getDonationSubscribers().stream()
                .filter(ds -> ds != null)
                .anyMatch(ds -> ds.getChatId().equals(chatId));
    }

    public void subscribe(DonationSubscriber donationSubscriber) {
        try {
            donationSubscribers.add(
                    mapper.writeValueAsString(donationSubscriber)
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        db.commit();
    }

    public void unsubsribeFromDonationSubscriptio(Long chatId) {
        this.donationSubscribers.removeIf(
                contact -> {
                    DonationSubscriber c = map(contact, DonationSubscriber.class);
                    return c != null && chatId.equals(c.getChatId());
                }
        );

        db.commit();
    }

    public List<Spend> getSpendsFrom(long from, long to) {
        return spends.stream()
                .map(s -> map(s, Spend.class))
                .filter(spend -> from <= spend.getTime() && to > spend.getTime())
                .collect(Collectors.toList());
    }

    public boolean isDonator(Long chatId) {
        return donators.stream()
                .map(s -> map(s, Donator.class))
                .anyMatch(s -> Long.compare(s.getChatId(), chatId) == 0);
    }

    public void becomeDonator(Donator donator) {
        try {
            donators.add(
                    mapper.writeValueAsString(donator)
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        db.commit();
    }

    public boolean isAdmin(Long chatId) {
        return getDonator(chatId)
                .map(d -> d.isAdmin())
                .orElse(false);
    }

    public void becomeAdmin(Long chatId) {
        getDonator(chatId)
                .ifPresent(donator -> {
                    stopDonating(chatId);
                    donator.setAdmin(true);
                    becomeDonator(donator);

                    db.commit();
                });
    }

    public void donateForPeople(Long chatId, Integer peopleCount) {
        getDonator(chatId).ifPresent(
                donator -> {
                    stopDonating(chatId);
                    donator.setPeopleCount(peopleCount);
                    becomeDonator(donator);

                    db.commit();
                }
        );
    }

    public void stopDonating(Long chatId) {
        this.donators.removeIf(
                contact -> {
                    Donator c = map(contact, Donator.class);
                    return c != null && chatId.equals(c.getChatId());
                }
        );

        db.commit();
    }

    public Optional<Donator> getDonator(Long chatId) {
        return getDonators().stream()
                .filter(d -> d.getChatId().equals(chatId))
                .findAny();
    }

    public void removeSpend(Spend spend) {
        this.spends.removeIf(
                s -> {
                    Spend spendFromList = map(s, Spend.class);
                    return spend.equals(spendFromList);
                }
        );

        db.commit();
    }

    public List<Donator> getDonators() {
        return donators.stream()
                .map(data -> map(data, Donator.class))
                .filter(e -> Objects.nonNull(e))
                .collect(toList());
    }

    private <T> T map(String data, Class<T> clz) {
        try {
            return mapper.readValue(data, clz);
        } catch (IOException e) {
            logger.error("Error", e);
            return null;
        }
    }
}
