package com.sni.dms.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "creating", schema = "dms", catalog = "")
public class CreatingEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "date")
    private Timestamp date;
    @Basic
    @Column(name = "user_id_user")
    private int userIdUser;
    @Basic
    @Column(name = "file_idfile")
    private int fileIdfile;

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getUserIdUser() {
        return userIdUser;
    }

    public void setUserIdUser(int userIdUser) {
        this.userIdUser = userIdUser;
    }

    public int getFileIdfile() {
        return fileIdfile;
    }

    public void setFileIdfile(int fileIdfile) {
        this.fileIdfile = fileIdfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreatingEntity that = (CreatingEntity) o;
        return userIdUser == that.userIdUser && fileIdfile == that.fileIdfile && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, userIdUser, fileIdfile);
    }
}
