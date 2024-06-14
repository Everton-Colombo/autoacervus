package com.example.autoacervus.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="UserSettings")
public class UserSettings {

    @Id
    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name="user")
    private User user;

    @Column(name="receiveEmails")
    private boolean receiveEmails = true;

    // No-args constructor required by ORM libraries
    public UserSettings() {}

    public UserSettings(User user) {
        this.user = user;
    }

    public UserSettings(User user, boolean receiveEmails) {
        this.user = user;
        this.receiveEmails = receiveEmails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReceiveEmails() {
        return receiveEmails;
    }

    public void setReceiveEmails(boolean receiveEmails) {
        this.receiveEmails = receiveEmails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return receiveEmails == that.receiveEmails && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, receiveEmails);
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "receiveEmails=" + receiveEmails +
                '}';
    }
}
