import { useState, useEffect } from 'react';
import { zgradaService } from '../api/zgradaService';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../utils/errorUtils';
import type { ZgradaDTO } from '../types/zgrada.types';
import type { StanDTO } from '../types/stan.types';
import LoadingSpinner from '../components/LoadingSpinner';

interface AptFormState { number: string; floor: string; }
interface ZgradaFormState { name: string; address: string; }

export default function ZgradePage() {
  const { showToast } = useToast();
  const [buildings, setZgradas] = useState<ZgradaDTO[]>([]);
  const [loading, setLoading] = useState(true);

  // Zgrada form
  const [showZgradaForm, setShowZgradaForm] = useState(false);
  const [editingZgrada, setEditingZgrada] = useState<ZgradaDTO | null>(null);
  const [buildingForm, setZgradaForm] = useState<ZgradaFormState>({ name: '', address: '' });
  const [buildingLoading, setZgradaLoading] = useState(false);

  // Expanded building apartments
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [apartmentsMap, setStansMap] = useState<Record<number, StanDTO[]>>({});

  // Stan form
  const [showStanForm, setShowStanForm] = useState<number | null>(null);
  const [editingStan, setEditingStan] = useState<StanDTO | null>(null);
  const [aptForm, setAptForm] = useState<AptFormState>({ number: '', floor: '' });
  const [aptLoading, setAptLoading] = useState(false);

  useEffect(() => {
    zgradaService.getAll()
      .then(setZgradas)
      .catch((err) => showToast(getErrorMessage(err, 'Failed to load buildings.'), 'error'))
      .finally(() => setLoading(false));
  }, [showToast]);

  async function toggleExpand(buildingId: number) {
    if (expandedId === buildingId) { setExpandedId(null); return; }
    setExpandedId(buildingId);
    if (!apartmentsMap[buildingId]) {
      try {
        const apts = await zgradaService.getStansByZgrada(buildingId);
        setStansMap((prev) => ({ ...prev, [buildingId]: apts }));
      } catch (err) {
        showToast(getErrorMessage(err, 'Failed to load apartments.'), 'error');
      }
    }
  }

  function openAddZgrada() {
    setEditingZgrada(null);
    setZgradaForm({ name: '', address: '' });
    setShowZgradaForm(true);
  }

  function openEditZgrada(b: ZgradaDTO) {
    setEditingZgrada(b);
    setZgradaForm({ name: b.name, address: b.address });
    setShowZgradaForm(true);
  }

  async function handleZgradaSubmit() {
    if (!buildingForm.name.trim() || !buildingForm.address.trim()) return;
    setZgradaLoading(true);
    try {
      if (editingZgrada) {
        const updated = await zgradaService.updateZgrada({ id: editingZgrada.id, ...buildingForm });
        setZgradas((prev) => prev.map((b) => (b.id === updated.id ? updated : b)));
        showToast('Building updated.', 'success');
      } else {
        const created = await zgradaService.addZgrada(buildingForm);
        setZgradas((prev) => [...prev, created]);
        showToast('Building added.', 'success');
      }
      setShowZgradaForm(false);
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to save building.'), 'error');
    } finally {
      setZgradaLoading(false);
    }
  }

  async function handleDeleteZgrada(id: number) {
    if (!confirm('Delete this building? This will also remove all its apartments.')) return;
    try {
      await zgradaService.deleteZgrada(id);
      setZgradas((prev) => prev.filter((b) => b.id !== id));
      if (expandedId === id) setExpandedId(null);
      showToast('Building deleted.', 'success');
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to delete building.'), 'error');
    }
  }

  function openAddStan(buildingId: number) {
    setEditingStan(null);
    setAptForm({ number: '', floor: '' });
    setShowStanForm(buildingId);
  }

  function openEditStan(apt: StanDTO) {
    setEditingStan(apt);
    setAptForm({ number: apt.number, floor: String(apt.floor) });
    setShowStanForm(apt.building.id);
  }

  async function handleStanSubmit(buildingId: number) {
    if (!aptForm.number.trim() || !aptForm.floor) return;
    setAptLoading(true);
    try {
      if (editingStan) {
        const updated = await zgradaService.updateStan({
          id: editingStan.id,
          number: aptForm.number.trim(),
          floor: Number(aptForm.floor),
          buildingId,
        });
        setStansMap((prev) => ({
          ...prev,
          [buildingId]: (prev[buildingId] ?? []).map((a) => (a.id === updated.id ? updated : a)),
        }));
        showToast('Apartment updated.', 'success');
      } else {
        const created = await zgradaService.addStan({
          number: aptForm.number.trim(),
          floor: Number(aptForm.floor),
          buildingId,
        });
        setStansMap((prev) => ({
          ...prev,
          [buildingId]: [...(prev[buildingId] ?? []), created],
        }));
        showToast('Apartment added.', 'success');
      }
      setShowStanForm(null);
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to save apartment.'), 'error');
    } finally {
      setAptLoading(false);
    }
  }

  async function handleDeleteStan(apt: StanDTO) {
    if (!confirm(`Delete apartment ${apt.number}?`)) return;
    try {
      await zgradaService.deleteStan(apt.id);
      setStansMap((prev) => ({
        ...prev,
        [apt.building.id]: (prev[apt.building.id] ?? []).filter((a) => a.id !== apt.id),
      }));
      showToast('Apartment deleted.', 'success');
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to delete apartment.'), 'error');
    }
  }

  if (loading) return <LoadingSpinner />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Zgradas</h1>
        <button
          onClick={openAddZgrada}
          className="px-4 py-2 bg-blue-600 text-white text-sm rounded font-medium hover:bg-blue-700 transition-colors"
        >
          + Add Building
        </button>
      </div>

      {/* Zgrada form modal */}
      {showZgradaForm && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">
              {editingZgrada ? 'Edit Building' : 'Add Building'}
            </h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input
                  type="text"
                  value={buildingForm.name}
                  onChange={(e) => setZgradaForm((f) => ({ ...f, name: e.target.value }))}
                  placeholder="e.g. Sunrise Tower"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                <input
                  type="text"
                  value={buildingForm.address}
                  onChange={(e) => setZgradaForm((f) => ({ ...f, address: e.target.value }))}
                  placeholder="e.g. 123 Main St, Belgrade"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
            <div className="flex gap-3 justify-end mt-5">
              <button
                onClick={() => setShowZgradaForm(false)}
                className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleZgradaSubmit}
                disabled={buildingLoading || !buildingForm.name.trim() || !buildingForm.address.trim()}
                className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {buildingLoading ? 'Saving...' : 'Save'}
              </button>
            </div>
          </div>
        </div>
      )}

      {buildings.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <p className="text-lg mb-2">No buildings yet.</p>
          <p className="text-sm">Click "Add Building" to get started.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {buildings.map((b) => (
            <div key={b.id} className="bg-white rounded-lg border border-gray-200">
              <div
                className="flex items-center justify-between px-5 py-4 cursor-pointer select-none"
                onClick={() => toggleExpand(b.id)}
              >
                <div>
                  <h3 className="font-semibold text-gray-800">{b.name}</h3>
                  <p className="text-sm text-gray-400">{b.address}</p>
                </div>
                <div className="flex items-center gap-3">
                  <button
                    onClick={(e) => { e.stopPropagation(); openEditZgrada(b); }}
                    className="text-xs text-blue-600 hover:underline"
                  >
                    Edit
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); handleDeleteZgrada(b.id); }}
                    className="text-xs text-red-500 hover:underline"
                  >
                    Delete
                  </button>
                  <span className="text-gray-400 text-xs ml-1">{expandedId === b.id ? '▲' : '▼'}</span>
                </div>
              </div>

              {expandedId === b.id && (
                <div className="border-t border-gray-100 px-5 py-4">
                  <div className="flex items-center justify-between mb-3">
                    <h4 className="text-xs font-semibold text-gray-500 uppercase tracking-wide">Stans</h4>
                    <button
                      onClick={() => openAddStan(b.id)}
                      className="text-xs text-blue-600 hover:underline font-medium"
                    >
                      + Add Stan
                    </button>
                  </div>

                  {/* Stan inline form */}
                  {showStanForm === b.id && (
                    <div className="bg-blue-50 rounded-lg p-4 mb-4 border border-blue-100">
                      <h5 className="text-sm font-medium text-gray-700 mb-3">
                        {editingStan ? 'Edit Apartment' : 'New Apartment'}
                      </h5>
                      <div className="grid grid-cols-2 gap-3 mb-3">
                        <div>
                          <label className="block text-xs text-gray-600 mb-1">Stan Number</label>
                          <input
                            type="text"
                            value={aptForm.number}
                            onChange={(e) => setAptForm((f) => ({ ...f, number: e.target.value }))}
                            placeholder="e.g. 4B"
                            className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                        <div>
                          <label className="block text-xs text-gray-600 mb-1">Floor</label>
                          <input
                            type="number"
                            value={aptForm.floor}
                            onChange={(e) => setAptForm((f) => ({ ...f, floor: e.target.value }))}
                            min={0}
                            placeholder="e.g. 3"
                            className="w-full border border-gray-300 rounded px-2 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <button
                          onClick={() => handleStanSubmit(b.id)}
                          disabled={aptLoading || !aptForm.number.trim() || !aptForm.floor}
                          className="px-3 py-1.5 text-xs bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
                        >
                          {aptLoading ? 'Saving...' : 'Save'}
                        </button>
                        <button
                          onClick={() => setShowStanForm(null)}
                          className="px-3 py-1.5 text-xs border border-gray-300 rounded hover:bg-gray-100 transition-colors"
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  )}

                  {!(apartmentsMap[b.id]) ? (
                    <p className="text-xs text-gray-400">Loading apartments...</p>
                  ) : apartmentsMap[b.id].length === 0 ? (
                    <p className="text-xs text-gray-400 italic">No apartments in this building.</p>
                  ) : (
                    <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-2">
                      {apartmentsMap[b.id].map((a) => (
                        <div
                          key={a.id}
                          className="flex items-center justify-between border border-gray-200 rounded px-3 py-2 bg-gray-50 text-sm"
                        >
                          <span className="text-gray-700">Apt {a.number} — Floor {a.floor}</span>
                          <div className="flex gap-2 ml-2 shrink-0">
                            <button
                              onClick={() => openEditStan(a)}
                              className="text-xs text-blue-600 hover:underline"
                            >
                              Edit
                            </button>
                            <button
                              onClick={() => handleDeleteStan(a)}
                              className="text-xs text-red-500 hover:underline"
                            >
                              Del
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
