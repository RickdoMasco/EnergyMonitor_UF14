import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DashboardApiService } from './dashboard-api.service';
import {
  DashboardData,
  DashboardScope,
  DashboardScopeType
} from '../interfaces/dashboard.interface';

@Component({
  selector: 'energy-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private readonly api = inject(DashboardApiService);

  readonly scopes = signal<DashboardScope[]>([]);
  readonly dashboard = signal<DashboardData | null>(null);
  readonly loadingScopes = signal(true);
  readonly loadingDashboard = signal(false);
  readonly error = signal<string | null>(null);

  selectedScopeType: DashboardScopeType = 'building';
  selectedScopeId = '';

  ngOnInit(): void {
    this.loadScopes();
  }

  loadScopes(): void {
    this.loadingScopes.set(true);
    this.error.set(null);
    this.api.getScopes().subscribe({
      next: (response) => {
        const scopes = Array.isArray(response)
          ? response
          : [
              ...(response.buildings ?? []).map(scope => ({ ...scope, type: 'building' as const })),
              ...(response.groups ?? []).map(scope => ({ ...scope, type: 'group' as const }))
            ];
        this.scopes.set(scopes);
        this.loadingScopes.set(false);
        const firstScope = scopes.find(scope => scope.type === this.selectedScopeType) ?? scopes[0];
        if (firstScope) {
          this.selectedScopeType = firstScope.type;
          this.selectedScopeId = String(firstScope.id);
          this.loadDashboard();
        }
      },
      error: () => {
        this.loadingScopes.set(false);
        this.error.set('Non è stato possibile recuperare gli ambiti dal servizio API.');
      }
    });
  }

  onScopeTypeChange(): void {
    const firstScope = this.scopes().find(scope => scope.type === this.selectedScopeType);
    this.selectedScopeId = firstScope ? String(firstScope.id) : '';
    this.loadDashboard();
  }

  loadDashboard(): void {
    if (!this.selectedScopeId) {
      this.dashboard.set(null);
      return;
    }

    this.loadingDashboard.set(true);
    this.error.set(null);
    this.api.getDashboard(this.selectedScopeType, this.selectedScopeId).subscribe({
      next: data => {
        this.dashboard.set(data);
        this.loadingDashboard.set(false);
      },
      error: () => {
        this.dashboard.set(null);
        this.loadingDashboard.set(false);
        this.error.set('Non è stato possibile aggiornare la dashboard. Verifica la connessione al servizio API.');
      }
    });
  }

  get filteredScopes(): DashboardScope[] {
    return this.scopes().filter(scope => scope.type === this.selectedScopeType);
  }

  get maxTrendValue(): number {
    return Math.max(...(this.dashboard()?.trend ?? []).map(point => point.value), 1);
  }

  trendWidth(value: number): string {
    return `${Math.max((value / this.maxTrendValue) * 100, 2)}%`;
  }

  formatDate(value: string | null | undefined): string {
    if (!value) {
      return 'mai disponibile';
    }
    return new Intl.DateTimeFormat('it-IT', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(new Date(value));
  }
}
