export interface ZgradaDTO {
  id: number;
  name: string;
  address: string;
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
