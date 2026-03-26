export type ApiErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
};

export type PagedResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
};
