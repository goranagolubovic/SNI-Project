import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import Keycloak from "keycloak-js";
let initOptions = {
  url: "http://localhost:8080/auth/",
  realm: "SNI",
  clientId: "dms2-react",
  onLoad: "login-required",
};

let keycloak = Keycloak(initOptions);

keycloak
  .init({ onLoad: "login-required" })
  .success((auth) => {
    if (!auth) {
      window.location.reload();
    } else {
      console.log();
      console.info("Authenticated");
      localStorage.setItem("TOKEN", JSON.stringify(keycloak.token));
    }
    keycloak.loadUserInfo().then((userInfo: any) => {
      localStorage.setItem(
        "USERNAME",
        JSON.stringify(userInfo.preferred_username)
      );
    });
  })
  .error(() => {
    console.error("Authenticated Failed");
  });
const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
