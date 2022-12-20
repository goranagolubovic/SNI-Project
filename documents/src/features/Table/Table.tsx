import React, { useCallback, useEffect, useState } from "react";
import DataTable from "react-data-table-component";
import RoutesWrapper from "../../routes/RoutesWrapper";
import classNames from "classnames";
import styles from "./Table.module.css";
import { Build, Backspace } from "@mui/icons-material";
import { Button } from "../../shared/components/Button/Button";
import { useHistory } from "react-router-dom";
import { deleteUser, fetchUsers } from "../../api/services/users";
import { SESSION_EXPIRED } from "../../constants";
const Table = () => {
  const [tokenExpired, setTokenExpired] = useState(false);
  const [tableContentChanged, setTableContentChanged] = useState(false);
  const [data, setData] = useState([]);
  const history = useHistory();
  const getUsers = useCallback(async () => {
    try {
      let res = await fetchUsers();
      console.log(res.status);
      if (res.status !== 403) {
        if (res.status === 401) {
          setTokenExpired(true);
        } else {
          const responseInfo = await res.json();
          console.log("response" + responseInfo);
          setData(responseInfo);
        }
      } else if (res.status === 403) {
        history.push("/");
      }
    } catch (err) {
      console.log(err);
    }
  }, [history]);
  useEffect(() => {
    getUsers();
    //setTableContentChanged(false);
  }, [getUsers, tableContentChanged]);
  const removeUser = async (username: string) => {
    try {
      let res = await deleteUser(username);
      let data = await res.json();
      if (data.status !== 403) {
        if (data.status === 401) {
          setTokenExpired(true);
        } else if (data.status === 404) {
          alert(data.message);
        } else {
          setTableContentChanged(!tableContentChanged);
        }
      } else if (data.status === 403) {
        history.push("/login");
      }
    } catch (err) {
      console.log(err);
    }
  };
  //   const data = [
  //     {
  //       username: "admin",
  //       role: "admin",
  //       homeDir: "nesto",
  //       ipAddress: "nesto",
  //       create: "yes",
  //       read: "no",
  //       update: "yes",
  //       delete: "no",
  //     },
  //   ];
  const columns = [
    {
      name: "Username",
      selector: (row: any) => row.username,
    },
    {
      name: "Role",
      selector: (row: any) => row.role,
    },
    {
      name: "Home directory",
      selector: (row: any) => row.userDir,
    },
    {
      name: "Ip Address",
      selector: (row: any) => row.ipAddress,
    },
    {
      name: "Create allowed",
      selector: (row: any) => row.isCreateApproved,
    },
    {
      name: "Read allowed",
      selector: (row: any) => row.isReadApproved,
    },
    {
      name: "Update allowed",
      selector: (row: any) => row.isUpdateApproved,
    },
    {
      name: "Delete allowed",
      selector: (row: any) => row.isDeleteApproved,
    },
    {
      name: "Change user data",
      selector: (row: any) => (
        <Button
          type="table"
          width="250px"
          height="35px"
          onClick={() =>
            history.push("/system-admin/update-user/" + row.username)
          }
        >
          <Build style={{ color: "grey", fontSize: 20 }}></Build> CHANGE
        </Button>
      ),
    },
    {
      name: "Delete user",
      selector: (row: any) => (
        <Button
          type="table"
          width="250px"
          height="35px"
          onClick={() => removeUser(row.username)}
        >
          <Backspace style={{ color: "grey", fontSize: 20 }}></Backspace> DELETE
        </Button>
      ),
    },
  ];
  return (
    <div>
      {!tokenExpired && (
        <DataTable
          className={classNames("rdt_TableCell", "rdt_TableCol")}
          columns={columns}
          data={data}
          fixedHeader
          highlightOnHover
        ></DataTable>
      )}
      {tokenExpired && (
        <div className={styles.sessionExpiration}>
          <p>{SESSION_EXPIRED}</p>
          <Button type="link" onClick={() => history.push("/")}>
            Sign in
          </Button>
        </div>
      )}
    </div>
  );
};

export default Table;
