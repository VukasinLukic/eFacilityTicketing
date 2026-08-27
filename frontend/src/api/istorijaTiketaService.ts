import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { IstorijaTiketaDTO } from '../types/istorijaTiketa.types';

export const istorijaTiketaService = {
  async getByTiket(ticketId: number): Promise<IstorijaTiketaDTO[]> {
    const res = await api.get<BackendResponse<{ history: IstorijaTiketaDTO[] }>>(`/ticket-history/byTicket/${ticketId}`);
    return res.data.data.history;
  },
};
