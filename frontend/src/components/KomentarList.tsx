import type { KomentarDTO } from '../types/komentar.types';
import { ROLE_LABELS, datumIVreme } from '../utils/labels';

export default function KomentarList({ comments }: { comments: KomentarDTO[] }) {
  if (comments.length === 0) {
    return <p className="text-sm text-gray-400 italic">Još nema komentara.</p>;
  }

  return (
    <div className="space-y-3">
      {comments.map((c) => (
        <div key={c.id} className="bg-gray-50 rounded-lg p-3 border border-gray-100">
          <div className="flex items-center justify-between mb-1">
            <span className="text-sm font-medium text-gray-700">
              {c.user.firstName} {c.user.lastName}
              <span className="ml-1 text-xs text-gray-400">({ROLE_LABELS[c.user.role]})</span>
            </span>
            <span className="text-xs text-gray-400">
              {datumIVreme(c.createdAt)}
            </span>
          </div>
          <p className="text-sm text-gray-600">{c.message}</p>
        </div>
      ))}
    </div>
  );
}
