import axios from 'axios';

const GENERIC_SERVER_MESSAGES = [
  'Došlo je do neočekivane greške. Pokušajte ponovo.',
  'Poslati podaci nisu u ispravnom formatu.',
];

export function getErrorMessage(error: unknown, fallback = 'Došlo je do neočekivane greške.'): string {
  if (axios.isAxiosError(error)) {
    const serverMessage = (error.response?.data as { message?: string })?.message;
    if (serverMessage && serverMessage.trim() && !GENERIC_SERVER_MESSAGES.includes(serverMessage.trim())) {
      return serverMessage;
    }
    return fallback;
  }
  if (error instanceof Error) return error.message;
  return fallback;
}
