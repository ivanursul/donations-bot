package org.ursul.bot.service;

import org.jetbrains.annotations.NotNull;
import org.ursul.bot.Dao;
import org.ursul.bot.domain.CumulatedSpend;
import org.ursul.bot.domain.Debt;
import org.ursul.bot.domain.Donator;
import org.ursul.bot.domain.Spend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class SpendsService {

    public static final String TAB = "     ";
    private Dao dao;

    public SpendsService(Dao dao) {
        this.dao = dao;
    }

    @NotNull
    public String getSpendMessage(long startOfMonth, long endOfMonth, Long requesterChatId) {
        List<Donator> donators = dao.getDonators();

        Map<Long, Donator> donatorsMap = dao.getDonators().stream()
                .collect(Collectors.toMap(Donator::getChatId, d -> d));

        List<Spend> spends = dao.getSpendsFrom(startOfMonth, endOfMonth);

        Integer totalSum = spends.stream().collect(Collectors.summingInt(spend -> spend.getAmount()));
        Integer totalNumberOfPeople = donators.stream().collect(Collectors.summingInt(d -> d.getPeopleCount()));

        double sumPerPerson = (double)totalSum / (double) totalNumberOfPeople;

        Map<Long, Integer> spendsPerDonator = spends.stream()
                .collect(
                        Collectors.groupingBy(
                                spend -> spend.getChatId(),
                                Collectors.summingInt(spend -> spend.getAmount())
                        )
                );

        List<CumulatedSpend> cumulatedSpends = donators.stream()
                .map(donator -> {
                    int additionalSpends = spendsPerDonator.containsKey(donator.getChatId()) ?
                            spendsPerDonator.get(donator.getChatId()) : 0;

                    double finalSpend = additionalSpends - (donator.getPeopleCount() * sumPerPerson);

                    return new CumulatedSpend(donator, finalSpend);
                }).sorted((s1, s2) -> Double.compare(s1.getFinalSpend(), s2.getFinalSpend()))
                .collect(toList());

        List<CumulatedSpend> debtorsFinal = cumulatedSpends.stream()
                .filter(cs -> cs.getFinalSpend() < 0)
                .sorted((d1, d2) -> Double.compare(d1.getFinalSpend(), d2.getFinalSpend()) * -1)
                .collect(toList());

        List<CumulatedSpend> donatorsFinal = cumulatedSpends.stream()
                .filter(cs -> cs.getFinalSpend() > 0)
                .sorted((d1, d2) -> Double.compare(d1.getFinalSpend(), d2.getFinalSpend()) * -1)
                .collect(toList());

        String debtorsMessage = debtorsFinal.stream()
                .map(cs -> TAB + cs.getDonator().constructName() + ": " + String.format("%.2f", cs.getFinalSpend()))
                .collect(joining("\n"));

        List<Debt> debts = new ArrayList<>();

        for (CumulatedSpend donator : donatorsFinal) {
            double overSpentByDonator = donator.getFinalSpend();

            for (CumulatedSpend debtor : debtorsFinal) {
                if (Math.abs(debtor.getFinalSpend()) > 0 && Math.abs(debtor.getFinalSpend()) < 1) {
                    break;
                }

                double debtByDebtor = debtor.getFinalSpend();

                if ((int)overSpentByDonator >= (int)Math.abs(debtByDebtor)) {

                    overSpentByDonator = overSpentByDonator - Math.abs(debtByDebtor);
                    debts.add(
                            new Debt(donator, debtor, Math.abs(debtByDebtor))
                    );
                    debtor.setFinalSpend(0);
                } else {
                    debtor.setFinalSpend(debtor.getFinalSpend() + overSpentByDonator);
                    debts.add(
                            new Debt(
                                    donator, debtor, overSpentByDonator
                            )
                    );
                    break;
                }
            }

        }

        List<Debt> peopleThatOweYou = debts.stream()
                .filter(debt -> debt.getSum() > 0.0)
                .filter(debt -> debt.getDonator().getDonator().getChatId().equals(requesterChatId))
                .collect(toList());

        List<Debt> youOweToPeople = debts.stream()
                .filter(debt -> debt.getSum() > 0.0)
                .filter(debt -> debt.getDebtor().getDonator().getChatId().equals(requesterChatId))
                .collect(toList());

        String oweYou = peopleThatOweYou.stream()
                .map(debtor -> new StringBuilder()
                        .append(TAB)
                        .append(debtor.getDebtor().getDonator().constructName())
                        .append(": ")
                        .append(String.format("%.2f", debtor.getSum())).toString())
                .collect(joining("\n"));

        String youOwe = youOweToPeople.stream()
                .map(debtor -> new StringBuilder()
                        .append(TAB)
                        .append(debtor.getDonator().getDonator().constructName())
                        .append(": ")
                        .append(String.format("%.2f", debtor.getSum())).toString())
                .collect(joining("\n"));

        Map<Long, List<Spend>> spendsPerDonatorList = spends.stream()
                .collect(
                        Collectors.groupingBy(
                                spend -> spend.getChatId(),
                                Collectors.toList()
                        )
                );

        String spendsDetails = spendsPerDonatorList.entrySet().stream()
                .map(entry -> {
                    Donator donator = donatorsMap.get(entry.getKey());
                    Integer totalPerDonator = spendsPerDonator.get(entry.getKey());

                    String spendsMessage = entry.getValue().stream()
                            .map(spend -> TAB + spend.getMessage() + ": " + spend.getAmount())
                            .collect(joining("\n"));

                    return "*" + donator.constructName() + "*: " + totalPerDonator + "\n" + spendsMessage;
                }).collect(joining("\n"));

        return new StringBuilder()
                .append("*Список боржників*:")
                .append("\n")
                .append(debtorsMessage)
                .append("\n\n")
                .append(!peopleThatOweYou.isEmpty() ?
                        "*Тобі винні*:\n" + oweYou + "\n\n" :
                        ""
                )
                .append(!youOweToPeople.isEmpty() ?
                        "*Ти винен*:\n" + youOwe + "\n\n" :
                        ""
                )
                .append("*Список витрат*:")
                .append("\n")
                .append(spendsDetails)
                .append("\n\n")
                .append("Загалом: " + totalSum + " срібняків\n")
                .append("З кожного: " + String.format( "%.2f", sumPerPerson) + " шекелів\n")
                .append("Кількість людей: " + donators.size())
                .toString();
    }

}
