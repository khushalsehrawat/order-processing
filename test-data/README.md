# Backend Test CSVs

Upload these files through the existing `/api/imports/orders` endpoint or the Angular upload screen.

- `orders_all_success.csv`: all rows should become `PLACED`.
- `orders_payment_failure_cancelled.csv`: payment fails, succeeded steps are undone, orders end `CANCELLED`.
- `orders_undo_failure_needs_attention.csv`: shipping execution fails, compensation also fails for one completed step, orders end `NEEDS_ATTENTION`; use retry compensation to finish cancellation.
- `orders_duplicate_reload.csv`: upload once, then upload the same file again; the second upload should count rows as duplicates.
- `orders_mixed_assignment_demo.csv`: mixed success, cancellation, and needs-attention cases for a fuller demo.

Reset all backend data for testing:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/test/reset
```

Run the coordinator reset after all services are started. It clears coordinator data and asks Order, Inventory, Payment, Shipping, and Notification services to clear their test data too.
