export type Role = 'TENANT' | 'MANAGER' | 'TECHNICIAN';

export interface UserDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: Role;
}
