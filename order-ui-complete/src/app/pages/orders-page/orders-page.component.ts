import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subject, interval, takeUntil } from 'rxjs';
import { OrderApiService } from '../../core/order-api.service';
import { OrderStatus, OrderWorkflow } from '../../models/order.model';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';

@Component({
  selector: 'app-orders-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    DatePipe,
    CurrencyPipe,
    StatusBadgeComponent
  ],
  templateUrl: './orders-page.component.html',
  styleUrl: './orders-page.component.css'
})
export class OrdersPageComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();

  orders: OrderWorkflow[] = [];
  filteredOrders: OrderWorkflow[] = [];
  selectedFile: File | null = null;
  page = 0;
  size = 20;
  totalElements = 0;
  totalPages = 0;
  loading = false;
  uploading = false;
  resetting = false;
  actionId: number | null = null;
  message = '';
  error = '';
  search = '';
  statusFilter = 'ALL';

  readonly statuses = [
    'ALL',
    'PENDING',
    'IN_PROGRESS',
    'COMPENSATING',
    'PLACED',
    'SHIPPED',
    'CANCELLED',
    'NEEDS_ATTENTION'
  ];

  constructor(private readonly api: OrderApiService) {}

  ngOnInit(): void {
    this.load();
    interval(5000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.load(false));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load(showLoader = true): void {
    if (showLoader) this.loading = true;
    this.api.getOrders(this.page, this.size).subscribe({
      next: response => {
        this.orders = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.applyFilters();
        this.loading = false;
      },
      error: error => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    const term = this.search.trim().toLowerCase();
    this.filteredOrders = this.orders.filter(order => {
      const statusMatches =
        this.statusFilter === 'ALL' || order.status === this.statusFilter;
      const textMatches =
        !term ||
        order.externalOrderId.toLowerCase().includes(term) ||
        order.customerId.toLowerCase().includes(term) ||
        order.productId.toLowerCase().includes(term);

      return statusMatches && textMatches;
    });
  }

  chooseFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] ?? null;
    this.message = '';
    this.error = '';
  }

  upload(): void {
    if (!this.selectedFile) return;
    this.uploading = true;
    this.api.importOrders(this.selectedFile).subscribe({
      next: response => {
        this.message =
          `Import complete: ${response.imported} new, ${response.duplicates} duplicate.`;
        this.uploading = false;
        this.selectedFile = null;
        setTimeout(() => this.load(), 1200);
      },
      error: error => {
        this.error = error.message;
        this.uploading = false;
      }
    });
  }

  resetData(): void {
    const confirmed = window.confirm(
      'Delete all test data from coordinator and backend services?'
    );
    if (!confirmed) return;

    this.resetting = true;
    this.message = '';
    this.error = '';
    this.api.resetTestData().subscribe({
      next: result => {
        const summary = Object.entries(result)
          .map(([service, status]) => `${service}: ${status}`)
          .join(', ');
        this.message = `Reset complete. ${summary}`;
        this.resetting = false;
        this.page = 0;
        this.load(false);
      },
      error: error => {
        this.error = error.message;
        this.resetting = false;
      }
    });
  }

  markShipped(order: OrderWorkflow): void {
    this.actionId = order.id;
    this.api.markShipped(order.id).subscribe({
      next: () => {
        this.message = `${order.externalOrderId} marked as shipped.`;
        this.actionId = null;
        this.load(false);
      },
      error: error => {
        this.error = error.message;
        this.actionId = null;
      }
    });
  }

  retryUndo(order: OrderWorkflow): void {
    this.actionId = order.id;
    this.api.retryCompensation(order.id).subscribe({
      next: () => {
        this.message = `Compensation retry started for ${order.externalOrderId}.`;
        this.actionId = null;
        setTimeout(() => this.load(false), 1000);
      },
      error: error => {
        this.error = error.message;
        this.actionId = null;
      }
    });
  }

  previous(): void {
    if (this.page === 0) return;
    this.page--;
    this.load();
  }

  next(): void {
    if (this.page + 1 >= this.totalPages) return;
    this.page++;
    this.load();
  }

  statusCount(status: OrderStatus): number {
    return this.orders.filter(order => order.status === status).length;
  }

  trackById(_: number, order: OrderWorkflow): number {
    return order.id;
  }
}
