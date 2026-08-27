import { useState, type FormEvent } from 'react';
import { komentarService } from '../api/komentarService';
import type { KomentarDTO } from '../types/komentar.types';

interface Props {
  ticketId: number;
  onAdded: (comment: KomentarDTO) => void;
}

export default function KomentarForm({ ticketId, onAdded }: Props) {
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!message.trim()) return;
    setLoading(true);
    setError('');
    try {
      const comment = await komentarService.addKomentar({ ticketId, message: message.trim() });
      onAdded(comment);
      setMessage('');
    } catch {
      setError('Failed to add comment.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="mt-4">
      {error && <p className="mb-2 text-sm text-red-600">{error}</p>}
      <textarea
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="Add a comment..."
        rows={3}
        className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
      />
      <div className="flex justify-end mt-2">
        <button
          type="submit"
          disabled={!message.trim() || loading}
          className="px-4 py-2 text-sm bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
        >
          {loading ? 'Posting...' : 'Post Comment'}
        </button>
      </div>
    </form>
  );
}
