export interface ZgradaDTO {
  id: number;
  name: string;
  address: string;
}

export interface PagedZgrade {
  buildings: ZgradaDTO[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface CreateZgradaRequest {
  name: string;
  address: string;
}

export interface UpdateZgradaRequest {
  id: number;
  name: string;
  address: string;
}
