import { Component, Input } from '@angular/core';
import { OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  template: `<span class="badge" [attr.data-status]="status">{{ label }}</span>`,
  styleUrl: './status-badge.component.css'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status!: OrderStatus | string;

  get label(): string {
    return this.status.replaceAll('_', ' ');
  }
}
