import { Routes } from '@angular/router';
import { OrdersPageComponent } from './pages/orders-page/orders-page.component';
import { OrderDetailsPageComponent } from './pages/order-details-page/order-details-page.component';

export const routes: Routes = [
  { path: '', component: OrdersPageComponent, title: 'Orders | OrderFlow' },
  { path: 'orders/:id', component: OrderDetailsPageComponent, title: 'Order Details | OrderFlow' },
  { path: '**', redirectTo: '' }
];
