import { useState, useEffect, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { tiketService } from '../api/tiketService';
import { zgradaService } from '../api/zgradaService';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../utils/errorUtils';
import type { StanDTO } from '../types/stan.types';
import type { Prioritet } from '../types/tiket.types';

const PRIORITIES: Prioritet[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];

export default function CreateTiketPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [apartments, setStans] = useState<StanDTO[]>([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPrioritet] = useState<Prioritet>('MEDIUM');
  const [apartmentId, setStanId] = useState<number | ''>('');
  const [loading, setLoading] = useState(false);
  const [aptsError, setAptsError] = useState('');

  useEffect(() => {
    zgradaService.getAllStans()
      .then(setStans)
      .catch(() => setAptsError('Could not load apartments. Please try again.'));
  }, []);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!apartmentId) return;
    if (title.trim().length < 5) {
      showToast('Title must be at least 5 characters.', 'error');
      return;
    }
    if (description.trim().length < 10) {
      showToast('Description must be at least 10 characters.', 'error');
      return;
    }
    setLoading(true);
    try {
      const ticket = await tiketService.createTiket({
        title: title.trim(),
        description: description.trim(),
        priority,
        apartmentId,
      });
      showToast('Ticket created successfully!', 'success');
      navigate(`/tickets/${ticket.id}`);
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to create ticket.'), 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">New Tiket</h1>

      {aptsError && (
        <div className="mb-4 text-sm text-red-700 bg-red-50 border border-red-200 p-3 rounded">
          {aptsError}
        </div>
      )}

      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              minLength={5}
              maxLength={200}
              placeholder="Brief summary of the issue"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
              minLength={10}
              rows={5}
              placeholder="Describe the issue in detail — location, what happened, how urgent it is..."
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prioritet</label>
              <select
                value={priority}
                onChange={(e) => setPrioritet(e.target.value as Prioritet)}
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {PRIORITIES.map((p) => (
                  <option key={p} value={p}>{p}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Stan</label>
              <select
                value={apartmentId}
                onChange={(e) => setStanId(Number(e.target.value))}
                required
                className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">-- Select apartment --</option>
                {apartments.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.building.name} — Apt {a.number} (Floor {a.floor})
                  </option>
                ))}
              </select>
            </div>
          </div>
          <div className="flex gap-3 pt-2">
            <button
              type="submit"
              disabled={loading || !apartmentId}
              className="px-6 py-2 bg-blue-600 text-white text-sm rounded font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
            >
              {loading ? 'Creating...' : 'Create Ticket'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/tickets')}
              className="px-6 py-2 border border-gray-300 text-gray-600 text-sm rounded hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
