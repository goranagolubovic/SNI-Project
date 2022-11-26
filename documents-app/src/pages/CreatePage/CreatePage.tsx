import React from "react";
import { useLocation } from "react-router-dom";

const CreatePage = () => {
  const location = useLocation();
  const path = location.pathname;
  return <div></div>;
};

export default CreatePage;
