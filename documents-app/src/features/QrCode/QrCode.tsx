import { DingtalkOutlined } from "@ant-design/icons";
import { Button, Typography } from "antd";
import styles from "./QrCode.module.css";
import React from "react";
import { QR_MESSAGE } from "../../constants";

export interface QrCodeProps {
  image: string;
  onClick?: () => void;
}
const QrCode = ({ image, onClick }: QrCodeProps) => {
  const { Title } = Typography;

  return (
    <div className={styles.container}>
      <Title level={5}>{QR_MESSAGE}</Title>
      <img src={image} width={200} />
      <Button
        onClick={onClick}
        shape="round"
        className="login-form-button"
        size="large"
      >
        Continue to login
      </Button>
    </div>
  );
};

export default QrCode;
