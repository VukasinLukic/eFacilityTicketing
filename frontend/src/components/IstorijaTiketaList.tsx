import type { IstorijaTiketaDTO } from '../types/istorijaTiketa.types';
import StatusTiketaBadge from './StatusTiketaBadge';

export default function IstorijaTiketaList({ history }: { history: IstorijaTiketaDTO[] }) {
  if (history.length === 0) {
    return <p className="text-sm text-gray-400 italic">Još nema izmena.</p>;
  }

  return (
    <div className="space-y-2">
      {history.map((h) => (
        <div key={h.id} className="flex items-center gap-3 text-sm py-2 border-b border-gray-100 last:border-0">
          <div className="flex items-center gap-2">
            {h.oldStatus ? (
              <>
                <StatusTiketaBadge status={h.oldStatus} />
                <span className="text-gray-400">→</span>
              </>
            ) : (
              <span className="text-gray-400 text-xs italic">Kreiran kao</span>
            )}
            <StatusTiketaBadge status={h.newStatus} />
          </div>
          <div className="flex-1 flex items-center justify-between text-xs text-gray-400">
            <span>{h.changedBy.firstName} {h.changedBy.lastName}</span>
            <span>{new Date(h.changedAt).toLocaleString()}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
