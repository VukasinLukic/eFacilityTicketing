import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { DashboardStatsDTO } from '../types/dashboard.types';

export const dashboardService = {
  async getStats(): Promise<DashboardStatsDTO> {
    const res = await api.get<BackendResponse<{ stats: DashboardStatsDTO }>>('/dashboard/stats');
    return res.data.data.stats;
  },
};
