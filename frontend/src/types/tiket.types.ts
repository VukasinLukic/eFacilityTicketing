import type { KorisnikDTO } from './korisnik.types';
import type { StanDTO } from './stan.types';

export type StatusTiketa = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CLOSED';
export type Prioritet = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface TiketDTO {
  id: number;
  title: string;
  description: string;
  status: StatusTiketa;
  priority: Prioritet;
  createdAt: string;
  updatedAt: string;
  tenant: KorisnikDTO;
  manager?: KorisnikDTO;
  technician?: KorisnikDTO;
  apartment: StanDTO;
}

export interface CreateTiketRequest {
  title: string;
  description: string;
  priority: Prioritet;
  apartmentId: number;
}

export interface AssignTiketRequest {
  ticketId: number;
  technicianId: number;
}

export interface UpdateStatusRequest {
  ticketId: number;
  newStatus: StatusTiketa;
}

export interface UpdatePrioritetRequest {
  ticketId: number;
  priority: Prioritet;
}

export interface TiketListParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: StatusTiketa | '';
  priority?: Prioritet | '';
  buildingId?: number;
  search?: string;
}

export interface TiketPageResponse {
  tickets: TiketDTO[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}
