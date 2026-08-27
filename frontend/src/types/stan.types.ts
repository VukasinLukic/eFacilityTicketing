import type { ZgradaDTO } from './zgrada.types';

export interface StanDTO {
  id: number;
  number: string;
  floor: number;
  building: ZgradaDTO;
}

export interface CreateStanRequest {
  number: string;
  floor: number;
  buildingId: number;
}

export interface UpdateStanRequest {
  id: number;
  number: string;
  floor: number;
  buildingId: number;
}
