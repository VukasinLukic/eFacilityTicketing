export class Response {
  timestamp: Date;
  statusCode: Number;
  status: string;
  reason: string;
  message: string;

  data!: { values: Object[]; value: Object };

  constructor(
    timestamp: Date,
    data: { values: Object[]; value: Object },
    reason: string,
    message: string,
    status: string,
    statusCode: number
  ) {
    this.timestamp = timestamp;
    this.data = data;
    this.reason = reason;
    this.message = message;
    this.status = status;
    this.statusCode = statusCode;
  }
}
