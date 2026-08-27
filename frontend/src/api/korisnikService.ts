import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { KorisnikDTO } from '../types/korisnik.types';

export const korisnikService = {
  async getTechnicians(): Promise<KorisnikDTO[]> {
    const res = await api.get<BackendResponse<{ technicians: KorisnikDTO[] }>>('/users/technicians');
    return res.data.data.technicians;
  },

  async getMe(): Promise<KorisnikDTO> {
    const res = await api.get<BackendResponse<{ user: KorisnikDTO }>>('/users/me');
    return res.data.data.user;
  },
};
