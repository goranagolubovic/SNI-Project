import React, { useEffect, useState } from "react";
import styles from "../SystemAdminPage/SystemAdmin.module.css";
import Header from "../../layouts/Header/Header";
import Table from "../../features/Table/Table";
import { Button } from "../../shared/components/Button/Button";
import PersonAdd from "@mui/icons-material/PersonAdd";
import { useHistory } from "react-router-dom";
import { logoutUser } from "../..";
import { ExitToApp } from "@mui/icons-material";
import ChangePasswordDialog from "../../features/ChangePasswordDialog/ChangePasswordDialog";
import { fetchUserInfo } from "../../api/services/users";
import { getUsername } from "../../util";
const SystemAdmin = () => {
  const history = useHistory();
  const [isPasswordChanged, setIsPasswordChanged] = useState(false);
  const [message, setMessage] = useState("");
  const [role, setRole] = useState("");
  const logout = () => {
    logoutUser();
  };
  const getUserInfo = async (username: string) => {
    console.log(username);
    try {
      const response = await fetchUserInfo(username);
      const respData = await response.json();

      if (respData.status == 200) {
        setIsPasswordChanged(respData.user.isPasswordChanged === 1);
        setRole(respData.user.role);
      } else {
        setMessage(respData.loginMessage);
        console.log(respData.loginMessage);
      }
    } catch (err) {
      console.log(err);
    }
  };
  useEffect(() => {
    getUserInfo(getUsername());
  }, []);
  return (
    <div className={styles.admin}>
      <Header />
      {isPasswordChanged && (
        <div>
          <div className={styles.addButton}>
            {role === "admin" && (
              <Button
                type="add"
                width="150px"
                height="35px"
                onClick={() => history.push("/system-admin/create-user")}
              >
                <PersonAdd style={{ color: "white", fontSize: 20 }}></PersonAdd>{" "}
                ADD USER
              </Button>
            )}
            <Button
              type="add"
              width="150px"
              height="35px"
              onClick={() => logout()}
            >
              <ExitToApp style={{ color: "white", fontSize: 20 }}></ExitToApp>{" "}
              LOGOUT
            </Button>
          </div>
          <Table />
        </div>
      )}
      {message !== "" && <p className={styles.error}>{message}</p>}
      {!isPasswordChanged && (
        <ChangePasswordDialog stateChanger={setIsPasswordChanged} />
      )}
    </div>
  );
};

export default SystemAdmin;
