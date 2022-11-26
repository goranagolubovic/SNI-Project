import React, { ReactElement } from "react";
import { Route } from "react-router-dom";

export interface RouteWrapperProps {
  component: React.FC;
  exact: boolean;
  path: string;
}

const RoutesWrapper = ({ component: Component, ...rest }: RouteWrapperProps) => {
  return (
    <Route
      {...rest}
      render={() => (
          <Component />
      )}
    ></Route>
  );
};

export default RoutesWrapper;
