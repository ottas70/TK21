package cz.cvut.fel.tk21.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.mail.ConfirmationToken;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "USER")
@NamedQueries({
        @NamedQuery(name = "User.getByEmail", query = "select u from User u where u.email=:email")
})
public class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    @NotBlank(message = "Surname is mandatory")
    private String surname;

    @Basic(optional = false)
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Basic(optional = false)
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Basic(optional = false)
    @Column(nullable = false)
    private boolean verifiedAccount;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private ConfirmationToken confirmationToken;

    public User() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerifiedAccount() {
        return verifiedAccount;
    }

    public void setVerifiedAccount(boolean verifiedAccount) {
        this.verifiedAccount = verifiedAccount;
    }

    public ConfirmationToken getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(ConfirmationToken confirmationToken) {
        this.confirmationToken = confirmationToken;
    }
}