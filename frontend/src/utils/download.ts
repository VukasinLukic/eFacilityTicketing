export function preuzmiFajl(blob: Blob, fallbackName: string, contentDisposition?: string) {
  const fileName = imeIzHedera(contentDisposition) || fallbackName;
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');

  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  URL.revokeObjectURL(url);
}

function imeIzHedera(contentDisposition?: string): string | null {
  if (!contentDisposition) return null;

  const utf8Match = /filename\*=UTF-8''([^;]+)/i.exec(contentDisposition);
  if (utf8Match) return decodeURIComponent(utf8Match[1].trim());

  const match = /filename="?([^";]+)"?/i.exec(contentDisposition);
  return match ? match[1].trim() : null;
}

export function danas(): string {
  return uIsoDatum(new Date());
}

export function pocetakNedelje(): string {
  const date = new Date();
  const dayOfWeek = (date.getDay() + 6) % 7;
  date.setDate(date.getDate() - dayOfWeek);
  return uIsoDatum(date);
}

export function pocetakMeseca(): string {
  const date = new Date();
  return uIsoDatum(new Date(date.getFullYear(), date.getMonth(), 1));
}

export function pocetakGodine(): string {
  const date = new Date();
  return uIsoDatum(new Date(date.getFullYear(), 0, 1));
}

function uIsoDatum(date: Date): string {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}
