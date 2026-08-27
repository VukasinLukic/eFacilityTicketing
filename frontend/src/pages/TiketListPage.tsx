import { useState, useEffect } from 'react';
import { tiketService } from '../api/tiketService';
import type { TiketDTO, StatusTiketa, Prioritet } from '../types/tiket.types';
import type { ZgradaDTO } from '../types/zgrada.types';
import { zgradaService } from '../api/zgradaService';
import { useAuth } from '../context/AuthContext';
import TiketCard from '../components/TiketCard';
import LoadingSpinner from '../components/LoadingSpinner';

const ALL_STATUSES: StatusTiketa[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CLOSED'];
const ALL_PRIORITIES: Prioritet[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
const SORT_OPTIONS = [
  { value: 'createdAt,desc', label: 'Newest first' },
  { value: 'createdAt,asc', label: 'Oldest first' },
  { value: 'priority,desc', label: 'Priority (high to low)' },
  { value: 'priority,asc', label: 'Priority (low to high)' },
  { value: 'status,asc', label: 'Status' },
];

export default function TiketListPage() {
  const { user } = useAuth();
  const isManager = user?.role === 'MANAGER';

  // --- Simple, unpaginated view for TENANT ("my tickets") and TECHNICIAN ("assigned to me") ---
  const [simpleTikets, setSimpleTikets] = useState<TiketDTO[]>([]);
  const [simpleLoading, setSimpleLoading] = useState(true);
  const [simpleError, setSimpleError] = useState('');
  const [statusFilter, setStatusFilter] = useState<StatusTiketa | ''>('');
  const [priorityFilter, setPrioritetFilter] = useState<Prioritet | ''>('');
  const [simpleSearch, setSimpleSearch] = useState('');

  useEffect(() => {
    if (isManager) return;
    async function load() {
      try {
        const data = user?.role === 'TECHNICIAN'
          ? await tiketService.getAssignedTikets()
          : await tiketService.getMyTikets();
        setSimpleTikets(data);
      } catch {
        setSimpleError('Failed to load tickets.');
      } finally {
        setSimpleLoading(false);
      }
    }
    load();
  }, [user, isManager]);

  const filtered = simpleTikets.filter((t) => {
    if (statusFilter && t.status !== statusFilter) return false;
    if (priorityFilter && t.priority !== priorityFilter) return false;
    if (simpleSearch && !t.title.toLowerCase().includes(simpleSearch.toLowerCase())) return false;
    return true;
  });

  // --- Server-side paginated / searched / sorted view for MANAGER ("all tickets") ---
  const [tickets, setTikets] = useState<TiketDTO[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [sort, setSort] = useState(SORT_OPTIONS[0].value);
  const [buildingId, setBuildingId] = useState<number | ''>('');
  const [zgrade, setZgrade] = useState<ZgradaDTO[]>([]);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Debounce the search box so we don't re-fetch on every keystroke.
  useEffect(() => {
    if (!isManager) return;
    const handle = setTimeout(() => {
      setSearch(searchInput);
      setPage(0);
    }, 350);
    return () => clearTimeout(handle);
  }, [searchInput, isManager]);

  useEffect(() => {
    if (!isManager) return;
    let cancelled = false;
    async function loadZgrade() {
      try {
        const data = await zgradaService.getAll();
        if (!cancelled) setZgrade(data);
      } catch {
        // The ticket list remains usable when buildings cannot be loaded.
      }
    }
    loadZgrade();
    return () => { cancelled = true; };
  }, [isManager]);

  useEffect(() => {
    if (!isManager) return;
    let cancelled = false;
    async function load() {
      setLoading(true);
      try {
        const res = await tiketService.getAllTikets({
          page,
          size: 10,
          sort,
          status: statusFilter,
          priority: priorityFilter,
          buildingId: buildingId || undefined,
          search,
        });
        if (cancelled) return;
        setTikets(res.tickets);
        setTotalPages(res.totalPages);
        setTotalElements(res.totalElements);
      } catch {
        if (!cancelled) setError('Failed to load tickets.');
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    load();
    return () => { cancelled = true; };
  }, [isManager, page, sort, statusFilter, priorityFilter, buildingId, search]);

  if (isManager) {
    return (
      <div>
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-800">All Tikets</h1>
          <span className="text-sm text-gray-400">{totalElements} ticket{totalElements !== 1 ? 's' : ''}</span>
        </div>

        <div className="flex flex-wrap gap-3 mb-6">
          <input
            type="text"
            placeholder="Search title or description..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <select
            value={statusFilter}
            onChange={(e) => { setStatusFilter(e.target.value as StatusTiketa | ''); setPage(0); }}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Statuses</option>
            {ALL_STATUSES.map((s) => (
              <option key={s} value={s}>{s.replace('_', ' ')}</option>
            ))}
          </select>
          <select
            value={priorityFilter}
            onChange={(e) => { setPrioritetFilter(e.target.value as Prioritet | ''); setPage(0); }}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All Priorities</option>
            {ALL_PRIORITIES.map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
          <select
            value={buildingId}
            onChange={(e) => { setBuildingId(e.target.value ? Number(e.target.value) : ''); setPage(0); }}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">All buildings</option>
            {zgrade.map((zgrada) => (
              <option key={zgrada.id} value={zgrada.id}>
                {zgrada.name} — {zgrada.address}
              </option>
            ))}
          </select>
          <select
            value={sort}
            onChange={(e) => { setSort(e.target.value); setPage(0); }}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {SORT_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>{o.label}</option>
            ))}
          </select>
          {(statusFilter || priorityFilter || buildingId || searchInput) && (
            <button
              onClick={() => { setStatusFilter(''); setPrioritetFilter(''); setBuildingId(''); setSearchInput(''); setSearch(''); setPage(0); }}
              className="text-sm text-gray-500 hover:text-gray-700"
            >
              Clear filters
            </button>
          )}
        </div>

        {error && <p className="text-red-600 mb-4">{error}</p>}

        {loading ? (
          <LoadingSpinner />
        ) : tickets.length === 0 ? (
          <p className="text-sm text-gray-400 italic">No tickets found.</p>
        ) : (
          <>
            <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
              {tickets.map((t) => (
                <TiketCard key={t.id} ticket={t} />
              ))}
            </div>

            {totalPages > 1 && (
              <div className="flex items-center justify-center gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="px-3 py-1.5 text-sm border border-gray-300 rounded disabled:opacity-40 hover:bg-gray-50"
                >
                  Previous
                </button>
                <span className="text-sm text-gray-500">
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                  className="px-3 py-1.5 text-sm border border-gray-300 rounded disabled:opacity-40 hover:bg-gray-50"
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    );
  }

  // TENANT / TECHNICIAN view
  if (simpleLoading) return <LoadingSpinner />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">
          {user?.role === 'TECHNICIAN' ? 'Assigned Tikets' : 'My Tikets'}
        </h1>
        <span className="text-sm text-gray-400">{filtered.length} ticket{filtered.length !== 1 ? 's' : ''}</span>
      </div>

      <div className="flex flex-wrap gap-3 mb-6">
        <input
          type="text"
          placeholder="Search by title..."
          value={simpleSearch}
          onChange={(e) => setSimpleSearch(e.target.value)}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as StatusTiketa | '')}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All Statuses</option>
          {ALL_STATUSES.map((s) => (
            <option key={s} value={s}>{s.replace('_', ' ')}</option>
          ))}
        </select>
        <select
          value={priorityFilter}
          onChange={(e) => setPrioritetFilter(e.target.value as Prioritet | '')}
          className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All Priorities</option>
          {ALL_PRIORITIES.map((p) => (
            <option key={p} value={p}>{p}</option>
          ))}
        </select>
        {(statusFilter || priorityFilter || simpleSearch) && (
          <button
            onClick={() => { setStatusFilter(''); setPrioritetFilter(''); setSimpleSearch(''); }}
            className="text-sm text-gray-500 hover:text-gray-700"
          >
            Clear filters
          </button>
        )}
      </div>

      {simpleError && <p className="text-red-600 mb-4">{simpleError}</p>}

      {filtered.length === 0 ? (
        <p className="text-sm text-gray-400 italic">No tickets found.</p>
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map((t) => (
            <TiketCard key={t.id} ticket={t} />
          ))}
        </div>
      )}
    </div>
  );
}
