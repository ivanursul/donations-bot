package org.ursul.bot.domain;

import java.io.Serializable;

public class Spend implements Serializable {

    private Long chatId;
    private Long time;
    private Integer amount;
    private String message;

    public Spend() {}

    public Spend(Long chatId, Long time, Integer amount, String message) {
        this.chatId = chatId;
        this.time = time;
        this.amount = amount;
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Spend spend = (Spend) o;

        if (chatId != null ? !chatId.equals(spend.chatId) : spend.chatId != null) return false;
        if (time != null ? !time.equals(spend.time) : spend.time != null) return false;
        if (amount != null ? !amount.equals(spend.amount) : spend.amount != null) return false;
        return message != null ? message.equals(spend.message) : spend.message == null;

    }

    @Override
    public int hashCode() {
        int result = chatId != null ? chatId.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Spend{");
        sb.append("chatId=").append(chatId);
        sb.append(", time=").append(time);
        sb.append(", amount=").append(amount);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
