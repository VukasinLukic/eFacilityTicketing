import type { StatusTiketa } from '../types/tiket.types';
import { STATUS_LABELS } from '../utils/labels';

const classNames: Record<StatusTiketa, string> = {
  OPEN:        'bg-blue-100 text-blue-800',
  ASSIGNED:    'bg-purple-100 text-purple-800',
  IN_PROGRESS: 'bg-yellow-100 text-yellow-800',
  COMPLETED:   'bg-green-100 text-green-800',
  CLOSED:      'bg-gray-100 text-gray-600',
};

export default function StatusTiketaBadge({ status }: { status: StatusTiketa }) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${classNames[status]}`}>
      {STATUS_LABELS[status]}
    </span>
  );
}
