import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  User,
} from '../models';

const TOKEN_KEY = 'fitquest_token';
const USER_KEY = 'fitquest_user';
const KEYCLOAK_TOKEN_KEY = 'fitquest_keycloak_token';
const KEYCLOAK_REFRESH_TOKEN_KEY = 'fitquest_keycloak_refresh_token';
const IDENTITY_PROVIDER_KEY = 'fitquest_identity_provider';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly api = `${environment.apiUrl}/auth`;

  readonly currentUser = signal<User | null>(this.hasValidApiToken() ? this.loadUser() : null);
  readonly isAuthenticated = signal(this.hasValidApiToken());

  login(credentials: LoginRequest): Observable<AuthResponse | null> {
    return this.http.post<AuthResponse>(`${this.api}/login`, credentials).pipe(
      tap((res) => this.setSession(res)),
      catchError(() => of(null))
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse | null> {
    return this.http.post<AuthResponse>(`${this.api}/register`, data).pipe(
      tap((res) => this.setSession(res)),
      catchError(() => of(null))
    );
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  handleUnauthorized(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    const token = localStorage.getItem(TOKEN_KEY);
    return token && !this.isJwtExpired(token) ? token : null;
  }

  getApiToken(): string | null {
    return this.getToken();
  }

  getKeycloakToken(): string | null {
    const token = localStorage.getItem(KEYCLOAK_TOKEN_KEY);
    return token && !this.isJwtExpired(token) ? token : null;
  }

  getIdentityProvider(): string | null {
    return localStorage.getItem(IDENTITY_PROVIDER_KEY);
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(KEYCLOAK_TOKEN_KEY);
    localStorage.removeItem(KEYCLOAK_REFRESH_TOKEN_KEY);
    localStorage.removeItem(IDENTITY_PROVIDER_KEY);
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
  }

  private setSession(res: AuthResponse): void {
    const token = res.accessToken ?? res.token ?? '';
    const user = {
      ...res.user,
      displayName: res.user.displayName ?? res.user.profile?.displayName,
    };
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    if (res.keycloakAccessToken) {
      localStorage.setItem(KEYCLOAK_TOKEN_KEY, res.keycloakAccessToken);
    }
    if (res.keycloakRefreshToken) {
      localStorage.setItem(KEYCLOAK_REFRESH_TOKEN_KEY, res.keycloakRefreshToken);
    }
    localStorage.setItem(IDENTITY_PROVIDER_KEY, res.identityProvider ?? 'local');
    this.currentUser.set(user);
    this.isAuthenticated.set(true);
  }

  private hasValidApiToken(): boolean {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!token || this.isJwtExpired(token)) {
      this.clearStoredSession();
      return false;
    }
    return true;
  }

  private clearStoredSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(KEYCLOAK_TOKEN_KEY);
    localStorage.removeItem(KEYCLOAK_REFRESH_TOKEN_KEY);
    localStorage.removeItem(IDENTITY_PROVIDER_KEY);
  }

  private isJwtExpired(token: string): boolean {
    const [, payload] = token.split('.');
    if (!payload) return true;
    try {
      const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = atob(normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '='));
      const exp = (JSON.parse(json) as { exp?: number }).exp;
      return typeof exp === 'number' ? exp * 1000 <= Date.now() : false;
    } catch {
      return true;
    }
  }

  private loadUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }
}
