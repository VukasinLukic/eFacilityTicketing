import { useState, useEffect } from 'react';
import { zgradaService } from '../api/zgradaService';
import { useToast } from '../context/ToastContext';
import { getErrorMessage } from '../utils/errorUtils';
import type { PagedZgrade } from '../types/zgrada.types';
import LoadingSpinner from '../components/LoadingSpinner';

const PAGE_SIZE = 5;

export default function ZgradeTabelaPage() {
  const { showToast } = useToast();
  const [page, setPage] = useState(0);
  const [data, setData] = useState<PagedZgrade | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    console.log(`[ZgradeTabelaPage] učitavam stranicu ${page}`);
    setLoading(true);
    zgradaService.getPaged(page, PAGE_SIZE)
      .then((res) => {
        console.log(`[ZgradeTabelaPage] dobijeno ${res.buildings.length} zgrada od ukupno ${res.totalElements}`);
        setData(res);
      })
      .catch((err) => showToast(getErrorMessage(err, 'Učitavanje zgrada nije uspelo.'), 'error'))
      .finally(() => setLoading(false));
  }, [page, showToast]);

  if (loading && !data) return <LoadingSpinner />;
  if (!data) return null;

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Zgrade (tabela sa paginacijom)</h1>

      <table className="w-full bg-white rounded-lg border border-gray-200 text-sm">
        <thead>
          <tr className="text-left text-gray-500 border-b border-gray-200">
            <th className="px-4 py-3 w-16">ID</th>
            <th className="px-4 py-3">Naziv</th>
            <th className="px-4 py-3">Adresa</th>
          </tr>
        </thead>
        <tbody>
          {data.buildings.map((b) => (
            <tr key={b.id} className="border-b border-gray-100 last:border-0">
              <td className="px-4 py-3 text-gray-400">{b.id}</td>
              <td className="px-4 py-3 font-medium text-gray-800">{b.name}</td>
              <td className="px-4 py-3 text-gray-600">{b.address}</td>
            </tr>
          ))}
          {data.buildings.length === 0 && (
            <tr>
              <td colSpan={3} className="px-4 py-6 text-center text-gray-400">Nema zgrada.</td>
            </tr>
          )}
        </tbody>
      </table>

      <div className="flex items-center justify-between mt-4 text-sm">
        <span className="text-gray-500">
          Stranica {data.page + 1} / {Math.max(data.totalPages, 1)} — ukupno {data.totalElements}
        </span>
        <div className="flex gap-2">
          <button
            onClick={() => setPage((p) => Math.max(p - 1, 0))}
            disabled={data.first || loading}
            className="px-3 py-1.5 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-40"
          >
            ← Prethodna
          </button>
          <button
            onClick={() => setPage((p) => p + 1)}
            disabled={data.last || loading}
            className="px-3 py-1.5 border border-gray-300 rounded hover:bg-gray-50 disabled:opacity-40"
          >
            Sledeća →
          </button>
        </div>
      </div>
    </div>
  );
}
