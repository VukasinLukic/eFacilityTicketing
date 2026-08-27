import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tiketService } from '../api/tiketService';
import { komentarService } from '../api/komentarService';
import { istorijaTiketaService } from '../api/istorijaTiketaService';
import type { TiketDTO, StatusTiketa, Prioritet } from '../types/tiket.types';
import type { KomentarDTO } from '../types/komentar.types';
import type { IstorijaTiketaDTO } from '../types/istorijaTiketa.types';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../utils/errorUtils';
import StatusTiketaBadge from '../components/StatusTiketaBadge';
import PrioritetBadge from '../components/PrioritetBadge';
import KomentarList from '../components/KomentarList';
import KomentarForm from '../components/KomentarForm';
import IstorijaTiketaList from '../components/IstorijaTiketaList';
import DodeliTehnicaraModal from '../components/DodeliTehnicaraModal';
import LoadingSpinner from '../components/LoadingSpinner';

const ALL_STATUSES: StatusTiketa[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CLOSED'];
const PRIORITY_OPTIONS: Prioritet[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

function getTechnicianStatusOptions(current: StatusTiketa): StatusTiketa[] {
  if (current === 'ASSIGNED') return ['ASSIGNED', 'IN_PROGRESS'];
  if (current === 'IN_PROGRESS') return ['IN_PROGRESS', 'COMPLETED'];
  return [current];
}

export default function TiketDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const [ticket, setTiket] = useState<TiketDTO | null>(null);
  const [comments, setKomentars] = useState<KomentarDTO[]>([]);
  const [history, setHistory] = useState<IstorijaTiketaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [statusUpdating, setStatusUpdating] = useState(false);
  const [priorityUpdating, setPrioritetUpdating] = useState(false);

  useEffect(() => {
    if (!id) return;
    const ticketId = Number(id);
    Promise.all([
      tiketService.getTiket(ticketId),
      komentarService.getByTiket(ticketId),
      istorijaTiketaService.getByTiket(ticketId),
    ])
      .then(([t, c, h]) => { setTiket(t); setKomentars(c); setHistory(h); })
      .catch((err) => setLoadError(getErrorMessage(err, 'Failed to load ticket.')))
      .finally(() => setLoading(false));
  }, [id]);

  async function handleStatusChange(newStatus: StatusTiketa) {
    if (!ticket || newStatus === ticket.status) return;
    setStatusUpdating(true);
    try {
      const updated = await tiketService.updateStatus({ ticketId: ticket.id, newStatus });
      setTiket(updated);
      const h = await istorijaTiketaService.getByTiket(ticket.id);
      setHistory(h);
      showToast('Status updated successfully.', 'success');
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to update status.'), 'error');
    } finally {
      setStatusUpdating(false);
    }
  }

  async function handlePrioritetChange(newPrioritet: Prioritet) {
    if (!ticket || newPrioritet === ticket.priority) return;
    setPrioritetUpdating(true);
    try {
      const updated = await tiketService.updatePrioritet({ ticketId: ticket.id, priority: newPrioritet });
      setTiket(updated);
      showToast('Priority updated.', 'success');
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to update priority.'), 'error');
    } finally {
      setPrioritetUpdating(false);
    }
  }

  if (loading) return <LoadingSpinner />;
  if (loadError) return (
    <div>
      <p className="text-red-600 mb-4">{loadError}</p>
      <button onClick={() => navigate(-1)} className="text-blue-600 hover:underline text-sm">← Back</button>
    </div>
  );
  if (!ticket) return null;

  const isManager = user?.role === 'MANAGER';
  const isTechnician = user?.role === 'TECHNICIAN';
  const isAssignedTech = isTechnician && ticket.technician?.id === user?.userId;
  const techOptions = getTechnicianStatusOptions(ticket.status);

  return (
    <div>
      <button onClick={() => navigate(-1)} className="text-sm text-blue-600 hover:underline mb-4 inline-block">
        ← Back
      </button>

      <div className="grid lg:grid-cols-3 gap-6">
        {/* Main content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Tiket info */}
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <div className="flex items-start justify-between gap-3 mb-3">
              <h1 className="text-xl font-bold text-gray-800">{ticket.title}</h1>
              <span className="text-sm text-gray-400 shrink-0">#{ticket.id}</span>
            </div>
            <div className="flex gap-2 mb-4">
              <StatusTiketaBadge status={ticket.status} />
              <PrioritetBadge priority={ticket.priority} />
            </div>
            <p className="text-sm text-gray-600 whitespace-pre-wrap">{ticket.description}</p>
          </div>

          {/* Komentars */}
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <h2 className="text-sm font-semibold text-gray-700 mb-4">
              Komentars ({comments.length})
            </h2>
            <KomentarList comments={comments} />
            <KomentarForm
              ticketId={ticket.id}
              onAdded={(c) => {
                setKomentars((prev) => [...prev, c]);
                showToast('Comment added.', 'success');
              }}
            />
          </div>

          {/* History */}
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <h2 className="text-sm font-semibold text-gray-700 mb-4">Status History</h2>
            <IstorijaTiketaList history={history} />
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-4">
          {/* Tiket meta */}
          <div className="bg-white rounded-lg border border-gray-200 p-4 text-sm space-y-3">
            <div>
              <span className="text-gray-400 block text-xs mb-0.5">Zgrada / Stan</span>
              <span className="text-gray-700 font-medium">
                {ticket.apartment.building.name} — Apt {ticket.apartment.number} (Floor {ticket.apartment.floor})
              </span>
            </div>
            <div>
              <span className="text-gray-400 block text-xs mb-0.5">Tenant</span>
              <span className="text-gray-700">{ticket.tenant.firstName} {ticket.tenant.lastName}</span>
            </div>
            {ticket.manager && (
              <div>
                <span className="text-gray-400 block text-xs mb-0.5">Manager</span>
                <span className="text-gray-700">{ticket.manager.firstName} {ticket.manager.lastName}</span>
              </div>
            )}
            {ticket.technician ? (
              <div>
                <span className="text-gray-400 block text-xs mb-0.5">Technician</span>
                <span className="text-gray-700">{ticket.technician.firstName} {ticket.technician.lastName}</span>
              </div>
            ) : (
              <div>
                <span className="text-gray-400 block text-xs mb-0.5">Technician</span>
                <span className="text-gray-400 italic text-xs">Not assigned</span>
              </div>
            )}
            <div>
              <span className="text-gray-400 block text-xs mb-0.5">Created</span>
              <span className="text-gray-700">{new Date(ticket.createdAt).toLocaleString()}</span>
            </div>
            <div>
              <span className="text-gray-400 block text-xs mb-0.5">Last Updated</span>
              <span className="text-gray-700">{new Date(ticket.updatedAt).toLocaleString()}</span>
            </div>
          </div>

          {/* Manager actions */}
          {isManager && (
            <div className="bg-white rounded-lg border border-gray-200 p-4 space-y-3">
              <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wide">Manager Actions</h3>

              {ticket.status === 'OPEN' && (
                <button
                  onClick={() => setShowAssignModal(true)}
                  className="w-full text-sm bg-purple-600 text-white py-2 rounded hover:bg-purple-700 transition-colors font-medium"
                >
                  Assign Technician
                </button>
              )}

              <div>
                <label className="block text-xs text-gray-500 mb-1">Update Status</label>
                <select
                  value={ticket.status}
                  onChange={(e) => handleStatusChange(e.target.value as StatusTiketa)}
                  disabled={statusUpdating}
                  className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                >
                  {ALL_STATUSES.map((s) => (
                    <option key={s} value={s}>{s.replace('_', ' ')}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs text-gray-500 mb-1">Update Prioritet</label>
                <select
                  value={ticket.priority}
                  onChange={(e) => handlePrioritetChange(e.target.value as Prioritet)}
                  disabled={priorityUpdating}
                  className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
                >
                  {PRIORITY_OPTIONS.map((p) => (
                    <option key={p} value={p}>{p}</option>
                  ))}
                </select>
              </div>
            </div>
          )}

          {/* Technician actions */}
          {isAssignedTech && techOptions.length > 1 && (
            <div className="bg-white rounded-lg border border-gray-200 p-4">
              <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wide mb-3">Update Status</h3>
              <select
                value={ticket.status}
                onChange={(e) => handleStatusChange(e.target.value as StatusTiketa)}
                disabled={statusUpdating}
                className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
              >
                {techOptions.map((s) => (
                  <option key={s} value={s}>{s.replace('_', ' ')}</option>
                ))}
              </select>
            </div>
          )}

          {isTechnician && !isAssignedTech && (
            <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3 text-xs text-yellow-700">
              You are not the assigned technician for this ticket.
            </div>
          )}
        </div>
      </div>

      {showAssignModal && (
        <DodeliTehnicaraModal
          ticket={ticket}
          onClose={() => setShowAssignModal(false)}
          onAssigned={(updated) => {
            setTiket(updated);
            setShowAssignModal(false);
            showToast('Technician assigned successfully.', 'success');
            istorijaTiketaService.getByTiket(ticket.id).then(setHistory).catch(() => null);
          }}
        />
      )}
    </div>
  );
}
