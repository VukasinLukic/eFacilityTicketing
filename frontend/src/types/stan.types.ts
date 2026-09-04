import type { ZgradaDTO } from './zgrada.types';
import type { KorisnikDTO } from './korisnik.types';

export interface StanDTO {
  id: number;
  number: string;
  floor: number;
  building: ZgradaDTO;
  tenant: KorisnikDTO | null;
}

export interface CreateStanRequest {
  number: string;
  floor: number;
  buildingId: number;
  tenantId?: number | null;
}

export interface UpdateStanRequest {
  id: number;
  number: string;
  floor: number;
  buildingId: number;
  tenantId?: number | null;
}
