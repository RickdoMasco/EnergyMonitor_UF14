import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DashboardData,
  DashboardScope,
  DashboardScopeType
} from '../interfaces/dashboard.interface';

interface DashboardScopesResponse {
  buildings?: DashboardScope[];
  groups?: DashboardScope[];
}

@Injectable({ providedIn: 'root' })
export class DashboardApiService {
  private readonly http = inject(HttpClient);

  getScopes(): Observable<DashboardScopesResponse | DashboardScope[]> {
    return this.http.get<DashboardScopesResponse | DashboardScope[]>('/api/dashboard/scopes');
  }

  getDashboard(scopeType: DashboardScopeType, scopeId: string | number): Observable<DashboardData> {
    const params = new HttpParams()
      .set('scopeType', scopeType)
      .set('scopeId', String(scopeId));

    return this.http.get<DashboardData>('/api/dashboard', { params });
  }
}
