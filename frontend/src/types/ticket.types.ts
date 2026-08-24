import type { UserDTO } from './user.types';
import type { ApartmentDTO } from './apartment.types';

export type TicketStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CLOSED';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface TicketDTO {
  id: number;
  title: string;
  description: string;
  status: TicketStatus;
  priority: Priority;
  createdAt: string;
  updatedAt: string;
  tenant: UserDTO;
  manager?: UserDTO;
  technician?: UserDTO;
  apartment: ApartmentDTO;
}

export interface CreateTicketRequest {
  title: string;
  description: string;
  priority: Priority;
  apartmentId: number;
}

export interface AssignTicketRequest {
  ticketId: number;
  technicianId: number;
}

export interface UpdateStatusRequest {
  ticketId: number;
  newStatus: TicketStatus;
}

export interface UpdatePriorityRequest {
  ticketId: number;
  newPriority: Priority;
}
