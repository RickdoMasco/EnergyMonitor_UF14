export type IngestionOutcome = 'ACCEPTED' | 'REJECTED';

export interface Sensor {
  id: number;
  ipAddress: string;
  macAddress: string;
  functioning: boolean;
  lastContactAt: string | null;
  lastOutcome: IngestionOutcome | null;
  lastOutcomeReason: string | null;
}
