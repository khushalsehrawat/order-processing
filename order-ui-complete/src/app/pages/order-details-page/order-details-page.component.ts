import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderApiService } from '../../core/order-api.service';
import { OrderDetails, OrderStep } from '../../models/order.model';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';

@Component({
  selector: 'app-order-details-page',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, CurrencyPipe, StatusBadgeComponent],
  templateUrl: './order-details-page.component.html',
  styleUrl: './order-details-page.component.css'
})
export class OrderDetailsPageComponent implements OnInit {
  details: OrderDetails | null = null;
  loading = true;
  actionRunning = false;
  error = '';
  message = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly api: OrderApiService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loading = true;
    this.api.getOrder(id).subscribe({
      next: response => {
        this.details = response;
        this.loading = false;
      },
      error: error => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  markShipped(): void {
    if (!this.details) return;
    this.actionRunning = true;
    this.api.markShipped(this.details.order.id).subscribe({
      next: () => {
        this.message = 'Order marked as shipped.';
        this.actionRunning = false;
        this.load();
      },
      error: error => {
        this.error = error.message;
        this.actionRunning = false;
      }
    });
  }

  retryUndo(): void {
    if (!this.details) return;
    this.actionRunning = true;
    this.api.retryCompensation(this.details.order.id).subscribe({
      next: () => {
        this.message = 'Compensation retry completed.';
        this.actionRunning = false;
        this.load();
      },
      error: error => {
        this.error = error.message;
        this.actionRunning = false;
      }
    });
  }

  executionSteps(): OrderStep[] {
    return this.details?.steps.filter(step => step.actionType === 'EXECUTE') ?? [];
  }

  compensationSteps(): OrderStep[] {
    return this.details?.steps.filter(step => step.actionType === 'COMPENSATE') ?? [];
  }
}
