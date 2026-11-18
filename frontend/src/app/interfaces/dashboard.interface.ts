export type DashboardScopeType = 'building' | 'group';

export interface DashboardScope {
  id: string | number;
  name: string;
  type: DashboardScopeType;
}

//kpi: key performance indicator
export interface DashboardKpi {
  label: string;
  value: number | string | null;
  unit?: string;
  status?: 'normal' | 'warning' | 'critical';
}

export interface DashboardTrendPoint {
  label: string;
  value: number;
  unit?: string;
}

export interface DashboardAnomaly {
  id: string | number;
  title: string;
  description?: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
  occurredAt: string;
  acknowledged?: boolean;
}

export interface DashboardData {
  kpis: DashboardKpi[];
  trend: DashboardTrendPoint[];
  anomalies: DashboardAnomaly[];
  lastUpdatedAt: string | null;
  stale: boolean;
}
