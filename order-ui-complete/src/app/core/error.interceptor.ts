import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const message =
        error.error?.error ||
        error.error?.message ||
        error.message ||
        'Unexpected request failure';

      return throwError(() => new Error(message));
    })
  );
};
