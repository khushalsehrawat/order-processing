import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ImportResponse,
  OrderDetails,
  OrderWorkflow,
  PageResponse
} from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderApiService {
  private readonly base = '/api';

  constructor(private readonly http: HttpClient) {}

  getOrders(page: number, size: number): Observable<PageResponse<OrderWorkflow>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<PageResponse<OrderWorkflow>>(
      `${this.base}/orders`,
      { params }
    );
  }

  getOrder(id: number): Observable<OrderDetails> {
    return this.http.get<OrderDetails>(`${this.base}/orders/${id}`);
  }

  importOrders(file: File): Observable<ImportResponse> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ImportResponse>(`${this.base}/imports/orders`, form);
  }

  markShipped(id: number): Observable<void> {
    return this.http.post<void>(`${this.base}/orders/${id}/mark-shipped`, {});
  }

  retryCompensation(id: number): Observable<void> {
    return this.http.post<void>(
      `${this.base}/orders/${id}/retry-compensation`,
      {}
    );
  }

  resetTestData(): Observable<Record<string, string>> {
    return this.http.post<Record<string, string>>(`${this.base}/test/reset`, {});
  }
}
