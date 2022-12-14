package com.sni.dms.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "dms", catalog = "")
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user")
    private int idUser;
    @Basic
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "password")
    private String password;
    @Basic
    @Column(name = "role")
    private String role;
    @Basic
    @Column(name = "user_dir")
    private String userDir;
    @Basic
    @Column(name = "ip_address")
    private String ipAddress;
    @Basic
    @Column(name = "is_create_approved")
    private Byte isCreateApproved;
    @Basic
    @Column(name = "is_read_approved")
    private Byte isReadApproved;
    @Basic
    @Column(name = "is_update_approved")
    private Byte isUpdateApproved;
    @Basic
    @Column(name = "is_delete_approved")
    private Byte isDeleteApproved;
    @Basic
    @Column(name = "is_deleted")
    private Byte isDeleted;
    @Basic
    @Column(name = "secret")
    private String secret;
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Byte getIsCreateApproved() {
        return isCreateApproved;
    }

    public void setIsCreateApproved(Byte isCreateApproved) {
        this.isCreateApproved = isCreateApproved;
    }

    public Byte getIsReadApproved() {
        return isReadApproved;
    }

    public void setIsReadApproved(Byte isReadApproved) {
        this.isReadApproved = isReadApproved;
    }

    public Byte getIsUpdateApproved() {
        return isUpdateApproved;
    }

    public void setIsUpdateApproved(Byte isUpdateApproved) {
        this.isUpdateApproved = isUpdateApproved;
    }

    public Byte getIsDeleteApproved() {
        return isDeleteApproved;
    }

    public void setIsDeleteApproved(Byte isDeleteApproved) {
        this.isDeleteApproved = isDeleteApproved;
    }
    public Byte getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return idUser == that.idUser && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(role, that.role) && Objects.equals(userDir, that.userDir) && Objects.equals(ipAddress, that.ipAddress) && Objects.equals(isCreateApproved, that.isCreateApproved) && Objects.equals(isReadApproved, that.isReadApproved) && Objects.equals(isUpdateApproved, that.isUpdateApproved) && Objects.equals(isDeleteApproved, that.isDeleteApproved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, username, password, role, userDir, ipAddress, isCreateApproved, isReadApproved, isUpdateApproved, isDeleteApproved);
    }
}
