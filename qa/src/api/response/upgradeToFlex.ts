/**
 
"upgradeToFlex": {
    "amount": null,
    "currency": null,
    "flexRateCode": null
}
 
 */
export class UpgradeToFlex {
  [key: string]: unknown;
  amount?: number | null;
  currency?: string | null;
  flexRateCode?: string | null;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): UpgradeToFlex {
    return new UpgradeToFlex(data);
  }
}
