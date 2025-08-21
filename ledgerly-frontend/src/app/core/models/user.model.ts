export enum Role {
  ADMIN = 'ADMIN',
  OWNER = 'OWNER',
  STAFF = 'STAFF'
}

export interface BasicUser {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  active: boolean;
}