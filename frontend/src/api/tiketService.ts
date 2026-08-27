import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type {
  TiketDTO,
  CreateTiketRequest,
  AssignTiketRequest,
  UpdateStatusRequest,
  UpdatePrioritetRequest,
  TiketListParams,
  TiketPageResponse,
} from '../types/tiket.types';

type TiketsRes = BackendResponse<{ tickets: TiketDTO[] }>;
type TiketRes = BackendResponse<{ ticket: TiketDTO }>;
type TiketPageRes = BackendResponse<TiketPageResponse>;

export const tiketService = {
  async getAllTikets(params: TiketListParams = {}): Promise<TiketPageResponse> {
    const { page = 0, size = 10, sort = 'createdAt,desc', status, priority, buildingId, search } = params;
    const res = await api.get<TiketPageRes>('/tickets/all', {
      params: {
        page,
        size,
        sort,
        status: status || undefined,
        priority: priority || undefined,
        buildingId: buildingId || undefined,
        search: search || undefined,
      },
    });
    return res.data.data;
  },

  async getMyTikets(): Promise<TiketDTO[]> {
    const res = await api.get<TiketsRes>('/tickets/my');
    return res.data.data.tickets;
  },

  async getAssignedTikets(): Promise<TiketDTO[]> {
    const res = await api.get<TiketsRes>('/tickets/assigned');
    return res.data.data.tickets;
  },

  async getTiket(id: number): Promise<TiketDTO> {
    const res = await api.get<TiketRes>(`/tickets/${id}`);
    return res.data.data.ticket;
  },

  async createTiket(data: CreateTiketRequest): Promise<TiketDTO> {
    const res = await api.post<TiketRes>('/tickets/create', data);
    return res.data.data.ticket;
  },

  async assignTechnician(data: AssignTiketRequest): Promise<TiketDTO> {
    const res = await api.put<TiketRes>('/tickets/assign', data);
    return res.data.data.ticket;
  },

  async updateStatus(data: UpdateStatusRequest): Promise<TiketDTO> {
    const res = await api.put<TiketRes>('/tickets/updateStatus', data);
    return res.data.data.ticket;
  },

  async updatePrioritet(data: UpdatePrioritetRequest): Promise<TiketDTO> {
    const res = await api.put<TiketRes>('/tickets/updatePriority', data);
    return res.data.data.ticket;
  },
};
