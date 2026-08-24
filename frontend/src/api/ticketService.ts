import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type {
  TicketDTO,
  CreateTicketRequest,
  AssignTicketRequest,
  UpdateStatusRequest,
  UpdatePriorityRequest,
} from '../types/ticket.types';

type TicketsRes = BackendResponse<{ tickets: TicketDTO[] }>;
type TicketRes = BackendResponse<{ ticket: TicketDTO }>;

export const ticketService = {
  async getAllTickets(): Promise<TicketDTO[]> {
    const res = await api.get<TicketsRes>('/tickets/all');
    return res.data.data.tickets;
  },

  async getMyTickets(): Promise<TicketDTO[]> {
    const res = await api.get<TicketsRes>('/tickets/my');
    return res.data.data.tickets;
  },

  async getAssignedTickets(): Promise<TicketDTO[]> {
    const res = await api.get<TicketsRes>('/tickets/assigned');
    return res.data.data.tickets;
  },

  async getTicket(id: number): Promise<TicketDTO> {
    const res = await api.get<TicketRes>(`/tickets/${id}`);
    return res.data.data.ticket;
  },

  async createTicket(data: CreateTicketRequest): Promise<TicketDTO> {
    const res = await api.post<TicketRes>('/tickets/create', data);
    return res.data.data.ticket;
  },

  async assignTechnician(data: AssignTicketRequest): Promise<TicketDTO> {
    const res = await api.post<TicketRes>('/tickets/assign', data);
    return res.data.data.ticket;
  },

  async updateStatus(data: UpdateStatusRequest): Promise<TicketDTO> {
    const res = await api.post<TicketRes>('/tickets/updateStatus', data);
    return res.data.data.ticket;
  },

  async updatePriority(data: UpdatePriorityRequest): Promise<TicketDTO> {
    const res = await api.post<TicketRes>('/tickets/updatePriority', data);
    return res.data.data.ticket;
  },
};
