export type OrderStatus =
  | 'PENDING'
  | 'IN_PROGRESS'
  | 'COMPENSATING'
  | 'PLACED'
  | 'SHIPPED'
  | 'CANCELLED'
  | 'NEEDS_ATTENTION';

export interface OrderWorkflow {
  id: number;
  externalOrderId: string;
  customerId: string;
  productId: string;
  quantity: number;
  amount: number;
  status: OrderStatus;
  failAt?: string | null;
  compFailAt?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface OrderStep {
  id: number;
  orderId: number;
  stepName: 'ORDER' | 'INVENTORY' | 'PAYMENT' | 'SHIPPING';
  actionType: 'EXECUTE' | 'COMPENSATE';
  status: string;
  idempotencyKey: string;
  attemptCount: number;
  lastError?: string | null;
  startedAt?: string | null;
  completedAt?: string | null;
}

export interface OrderEvent {
  id: number;
  orderId: number;
  eventType: string;
  stepName: string;
  message: string;
  attemptNumber: number;
  createdAt: string;
}

export interface OrderDetails {
  order: OrderWorkflow;
  steps: OrderStep[];
  events: OrderEvent[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ImportResponse {
  imported: number;
  duplicates: number;
}
