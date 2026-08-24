import type { UserDTO } from './user.types';

export interface CommentDTO {
  id: number;
  message: string;
  createdAt: string;
  ticket: { id: number };
  user: UserDTO;
}

export interface AddCommentRequest {
  ticketId: number;
  message: string;
}
