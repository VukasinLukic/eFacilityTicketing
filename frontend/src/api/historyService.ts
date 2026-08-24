import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { TicketHistoryDTO } from '../types/history.types';

export const historyService = {
  async getByTicket(ticketId: number): Promise<TicketHistoryDTO[]> {
    const res = await api.get<BackendResponse<{ history: TicketHistoryDTO[] }>>(`/ticket-history/byTicket/${ticketId}`);
    return res.data.data.history;
  },
};
