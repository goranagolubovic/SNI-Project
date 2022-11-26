export interface AddUserRequest {
    username: string;
    password: string;
    ipAddress: string;
    userDir: string;
    role: string;
    isCreateApproved?: number;
    isReadApproved?: number;
    isUpdateApproved?: number;
    isDeleteApproved?: number;
}