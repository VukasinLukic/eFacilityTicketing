import api from '../config/api';
import type { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth.types';
import type { BackendResponse } from '../types/api.types';

export const authService = {
  async login(data: LoginRequest): Promise<AuthResponse> {
    const res = await api.post<BackendResponse<{ auth: AuthResponse }>>('/auth/login', data);
    return res.data.data.auth;
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const res = await api.post<BackendResponse<{ auth: AuthResponse }>>('/auth/register', data);
    return res.data.data.auth;
  },
};
