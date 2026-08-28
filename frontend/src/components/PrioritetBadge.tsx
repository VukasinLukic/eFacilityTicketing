import type { Prioritet } from '../types/tiket.types';
import { PRIORITY_LABELS } from '../utils/labels';

const classNames: Record<Prioritet, string> = {
  LOW:    'bg-gray-100 text-gray-600',
  MEDIUM: 'bg-blue-100 text-blue-700',
  HIGH:   'bg-orange-100 text-orange-700',
  URGENT: 'bg-red-100 text-red-700',
};

export default function PrioritetBadge({ priority }: { priority: Prioritet }) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${classNames[priority]}`}>
      {PRIORITY_LABELS[priority]}
    </span>
  );
}
