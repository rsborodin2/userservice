package rborodin.skillgram.userservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedProductFilter", condition = "deleted = :isDeleted")
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;

    @Column(name = "secondname")
    private String secondname;

    @Column(name = "birth")
    private Date birth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;

    public User(String firstname, String surname, String secondname, Date birth, String gender, String email, String phone, Boolean deleted) {
        this.firstname = firstname;
        this.surname = surname;
        this.secondname = secondname;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.deleted = deleted;
    }

    public User(UUID id, String firstname, String surname, String secondname, Date birth, String gender, String email, String phone, Boolean deleted) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.secondname = secondname;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.deleted = deleted;
    }
}
