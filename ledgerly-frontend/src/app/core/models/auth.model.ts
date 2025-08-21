import { BasicUser } from "./user.model";

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface DecodedToken {
  sub: string;
  roles: string[];
  iat: number;
  exp: number;
}