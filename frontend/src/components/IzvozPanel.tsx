import { useState } from 'react';
import { tiketService } from '../api/tiketService';
import type { TiketExportParams } from '../types/tiket.types';
import { getErrorMessage } from '../utils/errorUtils';
import { danas, pocetakGodine, pocetakMeseca, pocetakNedelje } from '../utils/download';

interface Props {
  filteri?: Omit<TiketExportParams, 'from' | 'to'>;
  napomena?: string;
}

export default function IzvozPanel({ filteri, napomena }: Props) {
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [busy, setBusy] = useState<'excel' | 'pdf' | null>(null);
  const [error, setError] = useState('');

  function postaviOpseg(pocetak: string) {
    setFrom(pocetak);
    setTo(danas());
  }

  async function izvezi(format: 'excel' | 'pdf') {
    setBusy(format);
    setError('');
    try {
      const params: TiketExportParams = { ...filteri, from: from || undefined, to: to || undefined };
      if (format === 'excel') {
        await tiketService.exportExcel(params);
      } else {
        await tiketService.exportPdf(params);
      }
    } catch (err) {
      setError(getErrorMessage(err, 'Izvoz nije uspeo.'));
    } finally {
      setBusy(null);
    }
  }

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4 mb-6">
      <div className="flex items-center justify-between mb-3">
        <h2 className="text-sm font-semibold text-gray-700">Izvoz</h2>
        {napomena && <span className="text-xs text-gray-400">{napomena}</span>}
      </div>

      <div className="flex flex-wrap items-end gap-3">
        <div>
          <label className="block text-xs text-gray-500 mb-1">Od</label>
          <input
            type="date"
            value={from}
            max={to || undefined}
            onChange={(e) => setFrom(e.target.value)}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div>
          <label className="block text-xs text-gray-500 mb-1">Do</label>
          <input
            type="date"
            value={to}
            min={from || undefined}
            onChange={(e) => setTo(e.target.value)}
            className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => postaviOpseg(pocetakNedelje())}
            className="px-3 py-1.5 text-xs border border-gray-300 rounded hover:bg-gray-50"
          >
            Ova nedelja
          </button>
          <button
            type="button"
            onClick={() => postaviOpseg(pocetakMeseca())}
            className="px-3 py-1.5 text-xs border border-gray-300 rounded hover:bg-gray-50"
          >
            Ovaj mesec
          </button>
          <button
            type="button"
            onClick={() => postaviOpseg(pocetakGodine())}
            className="px-3 py-1.5 text-xs border border-gray-300 rounded hover:bg-gray-50"
          >
            Ova godina
          </button>
          {(from || to) && (
            <button
              type="button"
              onClick={() => { setFrom(''); setTo(''); }}
              className="px-3 py-1.5 text-xs text-gray-500 hover:text-gray-700"
            >
              Poništi period
            </button>
          )}
        </div>

        <div className="flex gap-2 ml-auto">
          <button
            type="button"
            onClick={() => izvezi('excel')}
            disabled={busy !== null}
            className="px-4 py-1.5 text-sm bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
          >
            {busy === 'excel' ? 'Izvozim...' : 'Izvezi Excel'}
          </button>
          <button
            type="button"
            onClick={() => izvezi('pdf')}
            disabled={busy !== null}
            className="px-4 py-1.5 text-sm bg-red-600 text-white rounded hover:bg-red-700 disabled:opacity-50"
          >
            {busy === 'pdf' ? 'Izvozim...' : 'Izvezi PDF'}
          </button>
        </div>
      </div>

      {error && <p className="text-red-600 text-sm mt-3">{error}</p>}
    </div>
  );
}
