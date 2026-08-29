import { useState, useEffect, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { tiketService } from '../api/tiketService';
import { zgradaService } from '../api/zgradaService';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../utils/errorUtils';
import type { StanDTO } from '../types/stan.types';
import type { Prioritet } from '../types/tiket.types';
import { PRIORITY_LABELS } from '../utils/labels';

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
      .catch(() => setAptsError('Učitavanje stanova nije uspelo. Pokušajte ponovo.'));
  }, []);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!apartmentId) return;
    if (title.trim().length < 5) {
      showToast('Naslov mora imati najmanje 5 karaktera.', 'error');
      return;
    }
    if (description.trim().length < 10) {
      showToast('Opis mora imati najmanje 10 karaktera.', 'error');
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
      showToast('Tiket je uspešno kreiran!', 'success');
      navigate(`/tickets/${ticket.id}`);
    } catch (err) {
      showToast(getErrorMessage(err, 'Kreiranje tiketa nije uspelo.'), 'error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="max-w-2xl">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Novi tiket</h1>

      {aptsError && (
        <div className="mb-4 text-sm text-red-700 bg-red-50 border border-red-200 p-3 rounded">
          {aptsError}
        </div>
      )}

      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Naslov</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              maxLength={200}
              placeholder="Kratak opis kvara"
              className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Opis</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={5}
              placeholder="Opišite kvar detaljno — gde je, šta se desilo, koliko je hitno..."
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
                  <option key={p} value={p}>{PRIORITY_LABELS[p]}</option>
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
                <option value="">-- Izaberite stan --</option>
                {apartments.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.building.name} — stan {a.number} ({a.floor}. sprat)
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
              {loading ? 'Kreiranje...' : 'Kreiraj tiket'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/tickets')}
              className="px-6 py-2 border border-gray-300 text-gray-600 text-sm rounded hover:bg-gray-50 transition-colors"
            >
              Odustani
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
