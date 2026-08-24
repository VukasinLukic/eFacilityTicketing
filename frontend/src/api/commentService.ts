import api from '../config/api';
import type { BackendResponse } from '../types/api.types';
import type { CommentDTO, AddCommentRequest } from '../types/comment.types';

export const commentService = {
  async getByTicket(ticketId: number): Promise<CommentDTO[]> {
    const res = await api.get<BackendResponse<{ comments: CommentDTO[] }>>(`/comments/byTicket/${ticketId}`);
    return res.data.data.comments;
  },

  async addComment(data: AddCommentRequest): Promise<CommentDTO> {
    const res = await api.post<BackendResponse<{ comment: CommentDTO }>>('/comments/add', data);
    return res.data.data.comment;
  },
};
