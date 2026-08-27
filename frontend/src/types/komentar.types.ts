import type { KorisnikDTO } from './korisnik.types';

export interface KomentarDTO {
  id: number;
  message: string;
  createdAt: string;
  ticket: { id: number };
  user: KorisnikDTO;
}

export interface AddKomentarRequest {
  ticketId: number;
  message: string;
}
