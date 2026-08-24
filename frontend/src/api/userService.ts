import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { UserDTO } from '../types/user.types';

export const userService = {
  async getTechnicians(): Promise<UserDTO[]> {
    const res = await api.get<BackendResponse<{ technicians: UserDTO[] }>>('/users/technicians');
    return res.data.data.technicians;
  },

  async getMe(): Promise<UserDTO> {
    const res = await api.get<BackendResponse<{ user: UserDTO }>>('/users/me');
    return res.data.data.user;
  },
};
