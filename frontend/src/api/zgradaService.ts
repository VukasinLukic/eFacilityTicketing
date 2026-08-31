import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { ZgradaDTO, PagedZgrade, CreateZgradaRequest, UpdateZgradaRequest } from '../types/zgrada.types';
import type { StanDTO, CreateStanRequest, UpdateStanRequest } from '../types/stan.types';

export const zgradaService = {
  async getAll(): Promise<ZgradaDTO[]> {
    const res = await api.get<BackendResponse<{ buildings: ZgradaDTO[] }>>('/buildings/all');
    return res.data.data.buildings;
  },

  // Server-side paginacija: baza vraća samo traženu stranicu.
  async getPaged(page: number, size = 5): Promise<PagedZgrade> {
    console.log(`[zgradaService] GET /buildings/paged?page=${page}&size=${size}`);
    const res = await api.get<BackendResponse<PagedZgrade>>('/buildings/paged', {
      params: { page, size },
    });
    console.log('[zgradaService] primljeno sa servera:', res.data.data);
    return res.data.data;
  },

  async getZgrada(id: number): Promise<ZgradaDTO> {
    const res = await api.get<BackendResponse<{ building: ZgradaDTO }>>(`/buildings/${id}`);
    return res.data.data.building;
  },

  async addZgrada(data: CreateZgradaRequest): Promise<ZgradaDTO> {
    const res = await api.post<BackendResponse<{ building: ZgradaDTO }>>('/buildings/add', data);
    return res.data.data.building;
  },

  async updateZgrada(data: UpdateZgradaRequest): Promise<ZgradaDTO> {
    const res = await api.put<BackendResponse<{ building: ZgradaDTO }>>('/buildings/update', data);
    return res.data.data.building;
  },

  async deleteZgrada(id: number): Promise<void> {
    await api.delete(`/buildings/delete/${id}`);
  },

  async getStansByZgrada(buildingId: number): Promise<StanDTO[]> {
    const res = await api.get<BackendResponse<{ apartments: StanDTO[] }>>(`/apartments/byBuilding/${buildingId}`);
    return res.data.data.apartments;
  },

  async getAllStans(): Promise<StanDTO[]> {
    const res = await api.get<BackendResponse<{ apartments: StanDTO[] }>>('/apartments/all');
    return res.data.data.apartments;
  },

  async addStan(data: CreateStanRequest): Promise<StanDTO> {
    const res = await api.post<BackendResponse<{ apartment: StanDTO }>>('/apartments/add', data);
    return res.data.data.apartment;
  },

  async updateStan(data: UpdateStanRequest): Promise<StanDTO> {
    const res = await api.put<BackendResponse<{ apartment: StanDTO }>>('/apartments/update', data);
    return res.data.data.apartment;
  },

  async deleteStan(id: number): Promise<void> {
    await api.delete(`/apartments/delete/${id}`);
  },
};
