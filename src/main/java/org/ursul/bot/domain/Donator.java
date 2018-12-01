package org.ursul.bot.domain;

import org.apache.commons.lang3.StringUtils;

public class Donator {

    private String username;
    private Long chatId;
    private String firstName;
    private String lastName;
    private Integer peopleCount;
    private boolean isAdmin;

    public Donator() {}

    public Donator(String username, Long chatId, String firstName, String lastName, Integer peopleCount, boolean isAdmin) {
        this.username = username;
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.peopleCount = peopleCount;
        this.isAdmin = isAdmin;
    }

    public String constructName() {
        return new StringBuilder()
                .append(StringUtils.isNotEmpty(firstName) ? firstName : "")
                .append(" ")
                .append(StringUtils.isNotEmpty(lastName) ? lastName : "")
                .toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Donator donator = (Donator) o;

        if (isAdmin != donator.isAdmin) return false;
        if (username != null ? !username.equals(donator.username) : donator.username != null) return false;
        if (chatId != null ? !chatId.equals(donator.chatId) : donator.chatId != null) return false;
        if (firstName != null ? !firstName.equals(donator.firstName) : donator.firstName != null) return false;
        if (lastName != null ? !lastName.equals(donator.lastName) : donator.lastName != null) return false;
        return peopleCount != null ? peopleCount.equals(donator.peopleCount) : donator.peopleCount == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (peopleCount != null ? peopleCount.hashCode() : 0);
        result = 31 * result + (isAdmin ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Donator{");
        sb.append("username='").append(username).append('\'');
        sb.append(", chatId=").append(chatId);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", peopleCount=").append(peopleCount);
        sb.append(", isAdmin=").append(isAdmin);
        sb.append('}');
        return sb.toString();
    }
}
