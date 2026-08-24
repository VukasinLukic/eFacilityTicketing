import type { BuildingDTO } from './building.types';

export interface ApartmentDTO {
  id: number;
  number: string;
  floor: number;
  building: BuildingDTO;
}

export interface CreateApartmentRequest {
  number: string;
  floor: number;
  buildingId: number;
}

export interface UpdateApartmentRequest {
  id: number;
  number: string;
  floor: number;
  buildingId: number;
}
