package com.example.autoacervus.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name="UserStats")
public class UserStats {
    @Id
    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name="user")
    private User user;

    @Column(name="renewalCount")
    private int renewalCount = 0;

    @Column(name="signupDate")
    private LocalDate signupDate = LocalDate.now();

    // No-args constructor required by ORM libraries
    public UserStats() {}

    public UserStats(User user) {
        this.user = user;
    }

    public UserStats(User user, int renewalCount, LocalDate signupDate) {
        this.user = user;
        this.renewalCount = renewalCount;
        this.signupDate = signupDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(int renewalCount) {
        this.renewalCount = renewalCount;
    }

    public LocalDate getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDate signupDate) {
        this.signupDate = signupDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStats userStats = (UserStats) o;
        return renewalCount == userStats.renewalCount && Objects.equals(user, userStats.user) && Objects.equals(signupDate, userStats.signupDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, renewalCount, signupDate);
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "renewalCount=" + renewalCount +
                ", signupDate=" + signupDate +
                '}';
    }
}
