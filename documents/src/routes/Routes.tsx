import React from "react";
import { BrowserRouter as Router, Switch } from "react-router-dom";
import AddUser from "../pages/AddUserPage/AddUser";
import SystemAdmin from "../pages/SystemAdminPage/SystemAdmin";
import UpdateUser from "../pages/UpdateUserPage/UpdateUser";
import RoutesWrapper from "./RoutesWrapper";

const Routes = () => {
  return (
    <Router>
      <Switch>
        <RoutesWrapper exact path="/" component={SystemAdmin} />
        <RoutesWrapper exact path="/system-admin" component={SystemAdmin} />
        <RoutesWrapper
          exact
          path="/system-admin/create-user"
          component={AddUser}
        />
        <RoutesWrapper
          exact
          path="/system-admin/update-user/:username"
          component={UpdateUser}
        />
      </Switch>
    </Router>
  );
};

export default Routes;
