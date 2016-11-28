package com.morrle.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RE_USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Serializable{
    private static final long serialVersionUID = -8791196323292191866L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[/w]{3,9}$")
    @Column(length = 50,unique = true,nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Pattern(regexp = "^[/w]*$")
    @Size(min = 60,max = 60)
    @Column(length = 60)
    private String password;

    @Email
    @Size(max = 100)
    @Column(length = 100,unique = true)
    private String email;

    @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @JsonIgnore
    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key",length = 20)
    private String resetKey;

    @Column(name = "create_date")
    private DateTime createdDate = DateTime.now();


    @Column(name = "reset_date")
    private DateTime resetDate = null;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "RE_USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public DateTime getResetDate() {
        return resetDate;
    }

    public void setResetDate(DateTime resetDate) {
        this.resetDate = resetDate;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!login.equals(user.login)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", activated=" + activated +
                ", activationKey='" + activationKey + '\'' +
                ", resetKey='" + resetKey + '\'' +
                ", createdDate=" + createdDate +
                ", resetDate=" + resetDate +
                ", authorities=" + authorities +
                '}';
    }
}
