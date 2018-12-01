package org.ursul.bot.domain;

public class DonationSubscriber {

    private Long chatId;

    public DonationSubscriber() {
    }

    public DonationSubscriber(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DonationSubscriber that = (DonationSubscriber) o;

        return chatId != null ? chatId.equals(that.chatId) : that.chatId == null;

    }

    @Override
    public int hashCode() {
        return chatId != null ? chatId.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DonationSubscriber{");
        sb.append("chatId=").append(chatId);
        sb.append('}');
        return sb.toString();
    }
}
