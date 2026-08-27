import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { KomentarDTO, AddKomentarRequest } from '../types/komentar.types';

export const komentarService = {
  async getByTiket(ticketId: number): Promise<KomentarDTO[]> {
    const res = await api.get<BackendResponse<{ comments: KomentarDTO[] }>>(`/comments/byTicket/${ticketId}`);
    return res.data.data.comments;
  },

  async addKomentar(data: AddKomentarRequest): Promise<KomentarDTO> {
    const res = await api.post<BackendResponse<{ comment: KomentarDTO }>>('/comments/add', data);
    return res.data.data.comment;
  },
};
