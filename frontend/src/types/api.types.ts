export interface BackendResponse<T = Record<string, unknown>> {
  message: string;
  data: T;
}
