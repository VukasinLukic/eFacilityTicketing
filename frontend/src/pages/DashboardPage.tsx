import { useState, useEffect } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell,
} from 'recharts';
import { dashboardService } from '../api/dashboardService';
import type { DashboardStatsDTO } from '../types/dashboard.types';
import LoadingSpinner from '../components/LoadingSpinner';
import { useAuth } from '../context/AuthContext';
import { tiketService } from '../api/tiketService';
import type { TiketDTO } from '../types/tiket.types';
import TiketCard from '../components/TiketCard';
import { STATUS_LABELS } from '../utils/labels';

const STATUS_COLORS: Record<string, string> = {
  OPEN: '#3b82f6',
  ASSIGNED: '#8b5cf6',
  IN_PROGRESS: '#f59e0b',
  COMPLETED: '#10b981',
  CLOSED: '#6b7280',
};

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStatsDTO | null>(null);
  const [recentTikets, setRecentTikets] = useState<TiketDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      try {
        let tickets: TiketDTO[] = [];
        if (user?.role === 'MANAGER') {
          const [s, t] = await Promise.all([
            dashboardService.getStats(),
            tiketService.getAllTikets({ page: 0, size: 6, sort: 'createdAt,desc' }),
          ]);
          setStats(s);
          tickets = t.tickets;
        } else if (user?.role === 'TENANT') {
          tickets = await tiketService.getMyTikets();
        } else if (user?.role === 'TECHNICIAN') {
          tickets = await tiketService.getAssignedTikets();
        }
        setRecentTikets(tickets.slice(0, 6));
      } catch {
        setError('Učitavanje podataka za kontrolnu tablu nije uspelo.');
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [user]);

  if (loading) return <LoadingSpinner />;
  if (error) return <p className="text-red-600">{error}</p>;

  const chartData = stats
    ? [
        { key: 'OPEN', name: STATUS_LABELS.OPEN, value: stats.openCount },
        { key: 'ASSIGNED', name: STATUS_LABELS.ASSIGNED, value: stats.assignedCount },
        { key: 'IN_PROGRESS', name: STATUS_LABELS.IN_PROGRESS, value: stats.inProgressCount },
        { key: 'COMPLETED', name: STATUS_LABELS.COMPLETED, value: stats.completedCount },
        { key: 'CLOSED', name: STATUS_LABELS.CLOSED, value: stats.closedCount },
      ]
    : [];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Kontrolna tabla</h1>

      {stats && (
        <>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
            {[
              { label: 'Ukupno', value: stats.totalCount, color: 'bg-gray-50 border-gray-200' },
              { label: STATUS_LABELS.OPEN, value: stats.openCount, color: 'bg-blue-50 border-blue-200' },
              { label: STATUS_LABELS.ASSIGNED, value: stats.assignedCount, color: 'bg-purple-50 border-purple-200' },
              { label: STATUS_LABELS.IN_PROGRESS, value: stats.inProgressCount, color: 'bg-yellow-50 border-yellow-200' },
              { label: STATUS_LABELS.COMPLETED, value: stats.completedCount, color: 'bg-green-50 border-green-200' },
              { label: STATUS_LABELS.CLOSED, value: stats.closedCount, color: 'bg-gray-50 border-gray-300' },
            ].map((s) => (
              <div key={s.label} className={`rounded-lg border p-4 text-center ${s.color}`}>
                <div className="text-2xl font-bold text-gray-800">{s.value}</div>
                <div className="text-xs text-gray-500 mt-1">{s.label}</div>
              </div>
            ))}
          </div>

          <div className="bg-white rounded-lg border border-gray-200 p-6 mb-8">
            <h2 className="text-sm font-semibold text-gray-700 mb-4">Tiketi po statusu</h2>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                  {chartData.map((entry) => (
                    <Cell key={entry.key} fill={STATUS_COLORS[entry.key] ?? '#6b7280'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </>
      )}

      <div>
        <h2 className="text-sm font-semibold text-gray-700 mb-3">
          {user?.role === 'MANAGER' ? 'Najnoviji tiketi' : 'Vaši tiketi'}
        </h2>
        {recentTikets.length === 0 ? (
          <p className="text-sm text-gray-400 italic">Nema tiketa.</p>
        ) : (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {recentTikets.map((t) => (
              <TiketCard key={t.id} ticket={t} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
