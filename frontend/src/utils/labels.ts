import type { StatusTiketa, Prioritet } from '../types/tiket.types';
import type { Uloga } from '../types/korisnik.types';

export const STATUS_LABELS: Record<StatusTiketa, string> = {
  OPEN: 'Otvoren',
  ASSIGNED: 'Dodeljen',
  IN_PROGRESS: 'U toku',
  COMPLETED: 'Završen',
  CLOSED: 'Zatvoren',
};

export const PRIORITY_LABELS: Record<Prioritet, string> = {
  LOW: 'Nizak',
  MEDIUM: 'Srednji',
  HIGH: 'Visok',
  URGENT: 'Hitan',
};

export const ROLE_LABELS: Record<Uloga, string> = {
  TENANT: 'Stanar',
  MANAGER: 'Menadžer',
  TECHNICIAN: 'Tehničar',
};

export function brojTiketa(n: number): string {
  return `${n} ${n === 1 ? 'tiket' : 'tiketa'}`;
}

const LOKALITET = 'sr-Latn-RS';

export function datumIVreme(iso: string): string {
  return new Date(iso).toLocaleString(LOKALITET);
}

export function datum(iso: string): string {
  return new Date(iso).toLocaleDateString(LOKALITET);
}
