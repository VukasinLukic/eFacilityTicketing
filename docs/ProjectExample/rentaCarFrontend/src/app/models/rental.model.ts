import { Client } from './client.model';
import { Car } from './car.model';
import { User } from './user.model';

export class Rental {
  public rentalId!: number;
  public startDate!: Date;
  public endDate!: Date;
  public notes!: string;
  public car!: Car;
  public client!: Client;
  public status!: number;
  public user: User | null = null;
}
