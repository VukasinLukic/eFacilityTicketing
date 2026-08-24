import type { UserDTO } from './user.types';
import type { TicketStatus } from './ticket.types';

export interface TicketHistoryDTO {
  id: number;
  oldStatus: TicketStatus | null;
  newStatus: TicketStatus;
  changedAt: string;
  changedBy: UserDTO;
}
