import type { TicketStatus } from '../types/ticket.types';

const config: Record<TicketStatus, { label: string; className: string }> = {
  OPEN:        { label: 'Open',        className: 'bg-blue-100 text-blue-800' },
  ASSIGNED:    { label: 'Assigned',    className: 'bg-purple-100 text-purple-800' },
  IN_PROGRESS: { label: 'In Progress', className: 'bg-yellow-100 text-yellow-800' },
  COMPLETED:   { label: 'Completed',   className: 'bg-green-100 text-green-800' },
  CLOSED:      { label: 'Closed',      className: 'bg-gray-100 text-gray-600' },
};

export default function TicketStatusBadge({ status }: { status: TicketStatus }) {
  const { label, className } = config[status];
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${className}`}>
      {label}
    </span>
  );
}
