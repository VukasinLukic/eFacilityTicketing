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
  TiketExportParams,
} from '../types/tiket.types';
import { preuzmiFajl } from '../utils/download';

type TiketsRes = BackendResponse<{ tickets: TiketDTO[] }>;
type TiketRes = BackendResponse<{ ticket: TiketDTO }>;
type TiketPageRes = BackendResponse<TiketPageResponse>;

function exportParams(params: TiketExportParams) {
  return {
    status: params.status || undefined,
    priority: params.priority || undefined,
    buildingId: params.buildingId || undefined,
    from: params.from || undefined,
    to: params.to || undefined,
  };
}

export const tiketService = {
  async exportExcel(params: TiketExportParams = {}): Promise<void> {
    const res = await api.get('/tickets/export/excel', {
      params: exportParams(params),
      responseType: 'blob',
    });
    preuzmiFajl(res.data as Blob, 'tiketi.xlsx', res.headers['content-disposition']);
  },

  async exportPdf(params: TiketExportParams = {}): Promise<void> {
    const res = await api.get('/tickets/export/pdf', {
      params: exportParams(params),
      responseType: 'blob',
    });
    preuzmiFajl(res.data as Blob, 'tiketi.pdf', res.headers['content-disposition']);
  },

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
