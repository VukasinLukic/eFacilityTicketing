import { useState, useEffect } from 'react';
import { korisnikService } from '../api/korisnikService';
import { tiketService } from '../api/tiketService';
import { getErrorMessage } from '../utils/errorUtils';
import type { KorisnikDTO } from '../types/korisnik.types';
import type { TiketDTO } from '../types/tiket.types';

interface Props {
  ticket: TiketDTO;
  onClose: () => void;
  onAssigned: (ticket: TiketDTO) => void;
}

export default function DodeliTehnicaraModal({ ticket, onClose, onAssigned }: Props) {
  const [technicians, setTechnicians] = useState<KorisnikDTO[]>([]);
  const [selectedId, setSelectedId] = useState<number | ''>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    korisnikService.getTechnicians().then(setTechnicians).catch((err) => setError(getErrorMessage(err, 'Failed to load technicians.')));
  }, []);

  async function handleAssign() {
    if (!selectedId) return;
    setLoading(true);
    setError('');
    try {
      const updated = await tiketService.assignTechnician({ ticketId: ticket.id, technicianId: selectedId });
      onAssigned(updated);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to assign technician.'));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <h2 className="text-lg font-semibold text-gray-800 mb-4">Assign Technician</h2>
        {error && <p className="mb-3 text-sm text-red-600">{error}</p>}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">Select Technician</label>
          <select
            value={selectedId}
            onChange={(e) => setSelectedId(Number(e.target.value))}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">-- Choose technician --</option>
            {technicians.map((t) => (
              <option key={t.id} value={t.id}>
                {t.firstName} {t.lastName} ({t.email})
              </option>
            ))}
          </select>
        </div>
        <div className="flex gap-3 justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm text-gray-600 border border-gray-300 rounded hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={handleAssign}
            disabled={!selectedId || loading}
            className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
          >
            {loading ? 'Assigning...' : 'Assign'}
          </button>
        </div>
      </div>
    </div>
  );
}
