package com.sni.dms.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "logs", schema = "dms", catalog = "")
public class LogsEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idlogs")
    private int idlogs;
    @Basic
    @Column(name = "date_time")
    private Timestamp dateTime;
    @Basic
    @Column(name = "user_id_user")
    private int userIdUser;
    @Basic
    @Column(name = "file_idfile")
    private int fileIdfile;
    @Basic
    @Column(name = "action")
    private String action;

    public int getIdlogs() {
        return idlogs;
    }

    public void setIdlogs(int idlogs) {
        this.idlogs = idlogs;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogsEntity that = (LogsEntity) o;
        return idlogs == that.idlogs && userIdUser == that.userIdUser && fileIdfile == that.fileIdfile && Objects.equals(dateTime, that.dateTime) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idlogs, dateTime, userIdUser, fileIdfile, action);
    }
}
