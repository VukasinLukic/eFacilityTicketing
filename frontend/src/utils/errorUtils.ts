import axios from 'axios';

export function getErrorMessage(error: unknown, fallback = 'Došlo je do neočekivane greške.'): string {
  if (axios.isAxiosError(error)) {
    return (error.response?.data as { message?: string })?.message ?? error.message ?? fallback;
  }
  if (error instanceof Error) return error.message;
  return fallback;
}
