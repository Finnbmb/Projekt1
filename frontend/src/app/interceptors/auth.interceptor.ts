import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Get the token from the auth service
    const token = this.authService.getToken();
    
    // Don't add Authorization header to auth endpoints (login, register, forgot-password)
    const isAuthEndpoint = req.url.includes('/api/v1/auth/login') || 
                          req.url.includes('/api/v1/auth/register') || 
                          req.url.includes('/api/v1/auth/forgot-password');
    
    // If we have a token and the request is to our API (but not auth endpoints), add the Authorization header
    if (token && req.url.includes('/api/') && !isAuthEndpoint) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      
      console.log('Adding Authorization header to request:', authReq.url);
      return next.handle(authReq);
    }
    
    // If no token, not an API request, or auth endpoint, proceed without modification
    console.log('Proceeding without Authorization header for:', req.url);
    return next.handle(req);
  }
}