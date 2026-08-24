import { useState, useEffect } from 'react';
import { buildingService } from '../api/buildingService';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../utils/errorUtils';
import type { BuildingDTO } from '../types/building.types';
import type { ApartmentDTO } from '../types/apartment.types';
import LoadingSpinner from '../components/LoadingSpinner';

interface AptFormState { number: string; floor: string; }
interface BuildingFormState { name: string; address: string; }

export default function BuildingsPage() {
  const { showToast } = useToast();
  const [buildings, setBuildings] = useState<BuildingDTO[]>([]);
  const [loading, setLoading] = useState(true);

  // Building form
  const [showBuildingForm, setShowBuildingForm] = useState(false);
  const [editingBuilding, setEditingBuilding] = useState<BuildingDTO | null>(null);
  const [buildingForm, setBuildingForm] = useState<BuildingFormState>({ name: '', address: '' });
  const [buildingLoading, setBuildingLoading] = useState(false);

  // Expanded building apartments
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [apartmentsMap, setApartmentsMap] = useState<Record<number, ApartmentDTO[]>>({});

  // Apartment form
  const [showApartmentForm, setShowApartmentForm] = useState<number | null>(null);
  const [editingApartment, setEditingApartment] = useState<ApartmentDTO | null>(null);
  const [aptForm, setAptForm] = useState<AptFormState>({ number: '', floor: '' });
  const [aptLoading, setAptLoading] = useState(false);

  useEffect(() => {
    buildingService.getAll()
      .then(setBuildings)
      .catch((err) => showToast(getErrorMessage(err, 'Failed to load buildings.'), 'error'))
      .finally(() => setLoading(false));
  }, [showToast]);

  async function toggleExpand(buildingId: number) {
    if (expandedId === buildingId) { setExpandedId(null); return; }
    setExpandedId(buildingId);
    if (!apartmentsMap[buildingId]) {
      try {
        const apts = await buildingService.getApartmentsByBuilding(buildingId);
        setApartmentsMap((prev) => ({ ...prev, [buildingId]: apts }));
      } catch (err) {
        showToast(getErrorMessage(err, 'Failed to load apartments.'), 'error');
      }
    }
  }

  function openAddBuilding() {
    setEditingBuilding(null);
    setBuildingForm({ name: '', address: '' });
    setShowBuildingForm(true);
  }

  function openEditBuilding(b: BuildingDTO) {
    setEditingBuilding(b);
    setBuildingForm({ name: b.name, address: b.address });
    setShowBuildingForm(true);
  }

  async function handleBuildingSubmit() {
    if (!buildingForm.name.trim() || !buildingForm.address.trim()) return;
    setBuildingLoading(true);
    try {
      if (editingBuilding) {
        const updated = await buildingService.updateBuilding({ id: editingBuilding.id, ...buildingForm });
        setBuildings((prev) => prev.map((b) => (b.id === updated.id ? updated : b)));
        showToast('Building updated.', 'success');
      } else {
        const created = await buildingService.addBuilding(buildingForm);
        setBuildings((prev) => [...prev, created]);
        showToast('Building added.', 'success');
      }
      setShowBuildingForm(false);
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to save building.'), 'error');
    } finally {
      setBuildingLoading(false);
    }
  }

  async function handleDeleteBuilding(id: number) {
    if (!confirm('Delete this building? This will also remove all its apartments.')) return;
    try {
      await buildingService.deleteBuilding(id);
      setBuildings((prev) => prev.filter((b) => b.id !== id));
      if (expandedId === id) setExpandedId(null);
      showToast('Building deleted.', 'success');
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to delete building.'), 'error');
    }
  }

  function openAddApartment(buildingId: number) {
    setEditingApartment(null);
    setAptForm({ number: '', floor: '' });
    setShowApartmentForm(buildingId);
  }

  function openEditApartment(apt: ApartmentDTO) {
    setEditingApartment(apt);
    setAptForm({ number: apt.number, floor: String(apt.floor) });
    setShowApartmentForm(apt.building.id);
  }

  async function handleApartmentSubmit(buildingId: number) {
    if (!aptForm.number.trim() || !aptForm.floor) return;
    setAptLoading(true);
    try {
      if (editingApartment) {
        const updated = await buildingService.updateApartment({
          id: editingApartment.id,
          number: aptForm.number.trim(),
          floor: Number(aptForm.floor),
          buildingId,
        });
        setApartmentsMap((prev) => ({
          ...prev,
          [buildingId]: (prev[buildingId] ?? []).map((a) => (a.id === updated.id ? updated : a)),
        }));
        showToast('Apartment updated.', 'success');
      } else {
        const created = await buildingService.addApartment({
          number: aptForm.number.trim(),
          floor: Number(aptForm.floor),
          buildingId,
        });
        setApartmentsMap((prev) => ({
          ...prev,
          [buildingId]: [...(prev[buildingId] ?? []), created],
        }));
        showToast('Apartment added.', 'success');
      }
      setShowApartmentForm(null);
    } catch (err) {
      showToast(getErrorMessage(err, 'Failed to save apartment.'), 'error');
    } finally {
      setAptLoading(false);
    }
  }

  async function handleDeleteApartment(apt: ApartmentDTO) {
    if (!confirm(`Delete apartment ${apt.number}?`)) return;
    try {
      await buildingService.deleteApartment(apt.id);
      setApartmentsMap((prev) => ({
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
        <h1 className="text-2xl font-bold text-gray-800">Buildings</h1>
        <button
          onClick={openAddBuilding}
          className="px-4 py-2 bg-blue-600 text-white text-sm rounded font-medium hover:bg-blue-700 transition-colors"
        >
          + Add Building
        </button>
      </div>

      {/* Building form modal */}
      {showBuildingForm && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">
              {editingBuilding ? 'Edit Building' : 'Add Building'}
            </h2>
            <div className="space-y-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input
                  type="text"
                  value={buildingForm.name}
                  onChange={(e) => setBuildingForm((f) => ({ ...f, name: e.target.value }))}
                  placeholder="e.g. Sunrise Tower"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                <input
                  type="text"
                  value={buildingForm.address}
                  onChange={(e) => setBuildingForm((f) => ({ ...f, address: e.target.value }))}
                  placeholder="e.g. 123 Main St, Belgrade"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
            <div className="flex gap-3 justify-end mt-5">
              <button
                onClick={() => setShowBuildingForm(false)}
                className="px-4 py-2 text-sm border border-gray-300 rounded hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleBuildingSubmit}
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
                    onClick={(e) => { e.stopPropagation(); openEditBuilding(b); }}
                    className="text-xs text-blue-600 hover:underline"
                  >
                    Edit
                  </button>
                  <button
                    onClick={(e) => { e.stopPropagation(); handleDeleteBuilding(b.id); }}
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
                    <h4 className="text-xs font-semibold text-gray-500 uppercase tracking-wide">Apartments</h4>
                    <button
                      onClick={() => openAddApartment(b.id)}
                      className="text-xs text-blue-600 hover:underline font-medium"
                    >
                      + Add Apartment
                    </button>
                  </div>

                  {/* Apartment inline form */}
                  {showApartmentForm === b.id && (
                    <div className="bg-blue-50 rounded-lg p-4 mb-4 border border-blue-100">
                      <h5 className="text-sm font-medium text-gray-700 mb-3">
                        {editingApartment ? 'Edit Apartment' : 'New Apartment'}
                      </h5>
                      <div className="grid grid-cols-2 gap-3 mb-3">
                        <div>
                          <label className="block text-xs text-gray-600 mb-1">Apartment Number</label>
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
                          onClick={() => handleApartmentSubmit(b.id)}
                          disabled={aptLoading || !aptForm.number.trim() || !aptForm.floor}
                          className="px-3 py-1.5 text-xs bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
                        >
                          {aptLoading ? 'Saving...' : 'Save'}
                        </button>
                        <button
                          onClick={() => setShowApartmentForm(null)}
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
                              onClick={() => openEditApartment(a)}
                              className="text-xs text-blue-600 hover:underline"
                            >
                              Edit
                            </button>
                            <button
                              onClick={() => handleDeleteApartment(a)}
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
