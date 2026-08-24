import { useState, useEffect } from 'react';
import { ticketService } from '../api/ticketService';
import type { TicketDTO, TicketStatus, Priority } from '../types/ticket.types';
import { useAuth } from '../context/AuthContext';
import TicketCard from '../components/TicketCard';
import LoadingSpinner from '../components/LoadingSpinner';

const ALL_STATUSES: TicketStatus[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CLOSED'];
const ALL_PRIORITIES: Priority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

export default function TicketListPage() {
  const { user } = useAuth();
  const [tickets, setTickets] = useState<TicketDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState<TicketStatus | ''>('');
  const [priorityFilter, setPriorityFilter] = useState<Priority | ''>('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    async function load() {
      try {
        let data: TicketDTO[];
        if (user?.role === 'MANAGER') data = await ticketService.getAllTickets();
        else if (user?.role === 'TECHNICIAN') data = await ticketService.getAssignedTickets();
        else data = await ticketService.getMyTickets();
        setTickets(data);
      } catch {
        setError('Failed to load tickets.');
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [user]);

  const filtered = tickets.filter((t) => {
    if (statusFilter && t.status !== statusFilter) return false;
    if (priorityFilter && t.priority !== priorityFilter) return false;
    if (search && !t.title.toLowerCase().includes(search.toLowerCase())) return false;
    return true;
  });

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">
          {user?.role === 'MANAGER' ? 'All Tickets' : user?.role === 'TECHNICIAN' ? 'Assigned Tickets' : 'My Tickets'}
        </h1>
        <span className="text-sm text-gray-400">{filtered.length} ticket{filtered.length !== 1 ? 's' : ''}</span>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-6">
        <input
          type="text"
          placeholder="Search by title..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as TicketStatus | '')}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All Statuses</option>
          {ALL_STATUSES.map((s) => (
            <option key={s} value={s}>{s.replace('_', ' ')}</option>
          ))}
        </select>
        <select
          value={priorityFilter}
          onChange={(e) => setPriorityFilter(e.target.value as Priority | '')}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All Priorities</option>
          {ALL_PRIORITIES.map((p) => (
            <option key={p} value={p}>{p}</option>
          ))}
        </select>
        {(statusFilter || priorityFilter || search) && (
          <button
            onClick={() => { setStatusFilter(''); setPriorityFilter(''); setSearch(''); }}
            className="text-sm text-gray-500 hover:text-gray-700"
          >
            Clear filters
          </button>
        )}
      </div>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      {filtered.length === 0 ? (
        <p className="text-sm text-gray-400 italic">No tickets found.</p>
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map((t) => (
            <TicketCard key={t.id} ticket={t} />
          ))}
        </div>
      )}
    </div>
  );
}
