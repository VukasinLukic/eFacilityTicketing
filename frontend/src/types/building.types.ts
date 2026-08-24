export interface BuildingDTO {
  id: number;
  name: string;
  address: string;
}

export interface CreateBuildingRequest {
  name: string;
  address: string;
}

export interface UpdateBuildingRequest {
  id: number;
  name: string;
  address: string;
}
