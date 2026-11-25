import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { DashboardApiService } from './dashboard-api.service';
import { Dashboard } from './dashboard';
import { DashboardData, DashboardScope } from '../interfaces/dashboard.interface';

const scopesResponse = {
  buildings: [{ id: 7, name: 'Sede centrale' }],
  groups: [{ id: 'hvac', name: 'Climatizzazione' }]
};

const dashboardData: DashboardData = {
  kpis: [
    { label: 'Consumo attuale', value: 42, unit: 'kW', status: 'normal' },
    { label: 'Dispositivi attivi', value: 8, unit: '' }
  ],
  trend: [
    { label: '10:00', value: 20, unit: 'kW' },
    { label: '11:00', value: 40, unit: 'kW' }
  ],
  anomalies: [
    {
      id: 12,
      title: 'Consumo sopra soglia',
      description: 'Valore oltre il limite configurato',
      severity: 'high',
      occurredAt: '2026-11-04T10:00:00Z'
    }
  ],
  lastUpdatedAt: '2026-11-04T11:00:00Z',
  stale: true
};

describe('Dashboard', () => {
  let fixture: ComponentFixture<Dashboard>;
  let component: Dashboard;
  let api: {
    getScopes: ReturnType<typeof vi.fn>;
    getDashboard: ReturnType<typeof vi.fn>;
  };

  function createComponent(): void {
    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(async () => {
    api = {
      getScopes: vi.fn().mockReturnValue(of(scopesResponse)),
      getDashboard: vi.fn().mockReturnValue(of(dashboardData))
    };

    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [{ provide: DashboardApiService, useValue: api }]
    }).compileComponents();
  });

  it('carica gli ambiti e seleziona il primo edificio disponibile', () => {
    createComponent();

    expect(api.getScopes).toHaveBeenCalledOnce();
    expect(component.selectedScopeType).toBe('building');
    expect(component.selectedScopeId).toBe('7');
    expect(api.getDashboard).toHaveBeenCalledWith('building', '7');
    expect(component.filteredScopes).toEqual([
      { id: 7, name: 'Sede centrale', type: 'building' }
    ] satisfies DashboardScope[]);
  });

  it('renderizza KPI, trend, anomalie e avviso di dati obsoleti', () => {
    createComponent();
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Consumo attuale');
    expect(element.textContent).toContain('42');
    expect(element.textContent).toContain('Trend recente');
    expect(element.textContent).toContain('Consumo sopra soglia');
    expect(element.textContent).toContain('Dati non aggiornati di recente');
    expect(element.querySelectorAll('.kpi-card')).toHaveLength(2);
    expect(element.querySelectorAll('.anomaly-row')).toHaveLength(1);
  });

  it('mostra lo stato vuoto quando l API non restituisce ambiti', () => {
    api.getScopes.mockReturnValue(of([]));
    createComponent();

    expect(component.selectedScopeId).toBe('');
    expect(api.getDashboard).not.toHaveBeenCalled();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Nessun ambito disponibile');
  });

  it('mostra un errore se il caricamento degli ambiti fallisce', () => {
    api.getScopes.mockReturnValue(throwError(() => new Error('offline')));
    createComponent();

    expect(component.error()).toContain('recuperare gli ambiti');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Servizio non raggiungibile');
  });

  it('mostra un errore se il caricamento della dashboard fallisce', () => {
    api.getDashboard.mockReturnValue(throwError(() => new Error('offline')));
    createComponent();

    expect(component.error()).toContain('aggiornare la dashboard');
    expect(component.dashboard()).toBeNull();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Servizio non raggiungibile');
  });

  it('cambia gruppo e richiede i dati del nuovo ambito', () => {
    createComponent();

    component.selectedScopeType = 'group';
    component.onScopeTypeChange();

    expect(component.selectedScopeId).toBe('hvac');
    expect(api.getDashboard).toHaveBeenLastCalledWith('group', 'hvac');
    expect(component.filteredScopes).toEqual([
      { id: 'hvac', name: 'Climatizzazione', type: 'group' }
    ] satisfies DashboardScope[]);
  });
});
