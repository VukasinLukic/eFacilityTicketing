import { CarModel } from "./car-model.model";

export class Car {
  public id!: number;
  public licensePlate!: string;
  public year!: number;
  public carModel!: CarModel;
  public status!: number;
  public notes!: string;
  public transmission!: string;
  public picture!: string;
  public pricePerDay!: number;
}
