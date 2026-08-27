export type Uloga = 'TENANT' | 'MANAGER' | 'TECHNICIAN';

export interface KorisnikDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: Uloga;
}
