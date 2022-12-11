import React from "react";
import { BrowserRouter as Router, Switch } from "react-router-dom";
import DocumentsPage from "../pages/DocumentPage/DocumentsPage";
import LoginPage from "../pages/LoginPage/LoginPage";
import LogsPage from "../pages/LogsPage/LogsPage";
import RoutesWrapper from "./RoutesWrapper";

const Routes = () => {
  return (
    <Router>
      <Switch>
        <RoutesWrapper exact path="/" component={LoginPage} />
        <RoutesWrapper exact path="/documents" component={DocumentsPage} />
        <RoutesWrapper exact path="/logs" component={LogsPage} />
      </Switch>
    </Router>
  );
};

export default Routes;
