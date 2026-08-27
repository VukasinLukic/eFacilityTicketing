import type { KorisnikDTO } from './korisnik.types';
import type { StatusTiketa } from './tiket.types';

export interface IstorijaTiketaDTO {
  id: number;
  oldStatus: StatusTiketa | null;
  newStatus: StatusTiketa;
  changedAt: string;
  changedBy: KorisnikDTO;
}
