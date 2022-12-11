package com.sni.dms.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "file", schema = "dms", catalog = "")
public class FileEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idfile")
    private int idfile;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "is_dir")
    private byte isDir;
    @Basic
    @Column(name = "root_dir")
    private Integer rootDir;
    @Basic
    @Column(name = "is_deleted")
    private byte isDeleted;
    @Basic
    @Column(name = "user_id_user")
    private int userIdUser;

    public int getIdfile() {
        return idfile;
    }

    public void setIdfile(int idfile) {
        this.idfile = idfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getIsDir() {
        return isDir;
    }

    public void setIsDir(byte isDir) {
        this.isDir = isDir;
    }

    public Integer getRootDir() {
        return rootDir;
    }

    public void setRootDir(Integer rootDir) {
        this.rootDir = rootDir;
    }

    public byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getUserIdUser() {
        return userIdUser;
    }

    public void setUserIdUser(int userIdUser) {
        this.userIdUser = userIdUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return idfile == that.idfile && isDir == that.isDir && isDeleted == that.isDeleted && userIdUser == that.userIdUser && Objects.equals(name, that.name) && Objects.equals(rootDir, that.rootDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idfile, name, isDir, rootDir, isDeleted, userIdUser);
    }
}
