import { useNavigate } from 'react-router-dom';
import type { TiketDTO } from '../types/tiket.types';
import { datum } from '../utils/labels';
import StatusTiketaBadge from './StatusTiketaBadge';
import PrioritetBadge from './PrioritetBadge';

export default function TiketCard({ ticket }: { ticket: TiketDTO }) {
  const navigate = useNavigate();

  return (
    <div
      onClick={() => navigate(`/tickets/${ticket.id}`)}
      className="bg-white rounded-lg border border-gray-200 p-4 cursor-pointer hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between gap-2 mb-2">
        <h3 className="font-semibold text-gray-800 text-sm line-clamp-1">{ticket.title}</h3>
        <span className="text-xs text-gray-400 shrink-0">#{ticket.id}</span>
      </div>
      <p className="text-xs text-gray-500 line-clamp-2 mb-3">{ticket.description}</p>
      <div className="flex items-center gap-2 flex-wrap">
        <StatusTiketaBadge status={ticket.status} />
        <PrioritetBadge priority={ticket.priority} />
      </div>
      <div className="mt-3 pt-3 border-t border-gray-100 flex items-center justify-between text-xs text-gray-400">
        <span>
          {ticket.apartment.building.name} — stan {ticket.apartment.number}
        </span>
        <span>{datum(ticket.createdAt)}</span>
      </div>
    </div>
  );
}
