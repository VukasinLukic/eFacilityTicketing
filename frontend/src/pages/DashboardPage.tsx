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

const STATUS_COLORS: Record<string, string> = {
  Open: '#3b82f6',
  Assigned: '#8b5cf6',
  'In Progress': '#f59e0b',
  Completed: '#10b981',
  Closed: '#6b7280',
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
        setError('Failed to load dashboard data.');
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
        { name: 'Open', value: stats.openCount },
        { name: 'Assigned', value: stats.assignedCount },
        { name: 'In Progress', value: stats.inProgressCount },
        { name: 'Completed', value: stats.completedCount },
        { name: 'Closed', value: stats.closedCount },
      ]
    : [];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>

      {stats && (
        <>
          {/* Stat cards */}
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
            {[
              { label: 'Total', value: stats.totalCount, color: 'bg-gray-50 border-gray-200' },
              { label: 'Open', value: stats.openCount, color: 'bg-blue-50 border-blue-200' },
              { label: 'Assigned', value: stats.assignedCount, color: 'bg-purple-50 border-purple-200' },
              { label: 'In Progress', value: stats.inProgressCount, color: 'bg-yellow-50 border-yellow-200' },
              { label: 'Completed', value: stats.completedCount, color: 'bg-green-50 border-green-200' },
              { label: 'Closed', value: stats.closedCount, color: 'bg-gray-50 border-gray-300' },
            ].map((s) => (
              <div key={s.label} className={`rounded-lg border p-4 text-center ${s.color}`}>
                <div className="text-2xl font-bold text-gray-800">{s.value}</div>
                <div className="text-xs text-gray-500 mt-1">{s.label}</div>
              </div>
            ))}
          </div>

          {/* Bar chart */}
          <div className="bg-white rounded-lg border border-gray-200 p-6 mb-8">
            <h2 className="text-sm font-semibold text-gray-700 mb-4">Tikets by Status</h2>
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                  {chartData.map((entry) => (
                    <Cell key={entry.name} fill={STATUS_COLORS[entry.name] ?? '#6b7280'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </>
      )}

      {/* Recent tickets */}
      <div>
        <h2 className="text-sm font-semibold text-gray-700 mb-3">
          {user?.role === 'MANAGER' ? 'Recent Tikets' : 'Your Tikets'}
        </h2>
        {recentTikets.length === 0 ? (
          <p className="text-sm text-gray-400 italic">No tickets found.</p>
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
