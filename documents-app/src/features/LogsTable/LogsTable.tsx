import React, { useCallback, useEffect, useState } from "react";
import DataTable from "react-data-table-component";
import RoutesWrapper from "../../routes/RoutesWrapper";
import classNames from "classnames";
import styles from "../LogsTable/LogsTable.module.css";
import { useHistory } from "react-router-dom";
import { fetchLogs } from "../../api/services/logs";
const LogsTable = () => {
  const [tokenExpired, setTokenExpired] = useState(false);
  const [data, setData] = useState([]);
  const history = useHistory();
  const getLogs = useCallback(async () => {
    try {
      let res = await fetchLogs();
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
    getLogs();
    //setTableContentChanged(false);
  }, [getLogs]);
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
      name: "File path",
      selector: (row: any) => row.filePath,
    },
    {
      name: "Date and time",
      selector: (row: any) => row.dateTime,
    },
    {
      name: "Action",
      selector: (row: any) => row.action,
    },
  ];
  return (
    <DataTable
      className={classNames("rdt_TableCell", "rdt_TableCol")}
      columns={columns}
      data={data}
      fixedHeader
      highlightOnHover
    ></DataTable>
  );
};

export default LogsTable;
