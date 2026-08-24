import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { BuildingDTO, CreateBuildingRequest, UpdateBuildingRequest } from '../types/building.types';
import type { ApartmentDTO, CreateApartmentRequest, UpdateApartmentRequest } from '../types/apartment.types';

export const buildingService = {
  async getAll(): Promise<BuildingDTO[]> {
    const res = await api.get<BackendResponse<{ buildings: BuildingDTO[] }>>('/buildings/all');
    return res.data.data.buildings;
  },

  async getBuilding(id: number): Promise<BuildingDTO> {
    const res = await api.get<BackendResponse<{ building: BuildingDTO }>>(`/buildings/${id}`);
    return res.data.data.building;
  },

  async addBuilding(data: CreateBuildingRequest): Promise<BuildingDTO> {
    const res = await api.post<BackendResponse<{ building: BuildingDTO }>>('/buildings/add', data);
    return res.data.data.building;
  },

  async updateBuilding(data: UpdateBuildingRequest): Promise<BuildingDTO> {
    const res = await api.post<BackendResponse<{ building: BuildingDTO }>>('/buildings/update', data);
    return res.data.data.building;
  },

  async deleteBuilding(id: number): Promise<void> {
    await api.post(`/buildings/delete/${id}`);
  },

  async getApartmentsByBuilding(buildingId: number): Promise<ApartmentDTO[]> {
    const res = await api.get<BackendResponse<{ apartments: ApartmentDTO[] }>>(`/apartments/byBuilding/${buildingId}`);
    return res.data.data.apartments;
  },

  async getAllApartments(): Promise<ApartmentDTO[]> {
    const res = await api.get<BackendResponse<{ apartments: ApartmentDTO[] }>>('/apartments/all');
    return res.data.data.apartments;
  },

  async addApartment(data: CreateApartmentRequest): Promise<ApartmentDTO> {
    const res = await api.post<BackendResponse<{ apartment: ApartmentDTO }>>('/apartments/add', data);
    return res.data.data.apartment;
  },

  async updateApartment(data: UpdateApartmentRequest): Promise<ApartmentDTO> {
    const res = await api.post<BackendResponse<{ apartment: ApartmentDTO }>>('/apartments/update', data);
    return res.data.data.apartment;
  },

  async deleteApartment(id: number): Promise<void> {
    await api.post(`/apartments/delete/${id}`);
  },
};
