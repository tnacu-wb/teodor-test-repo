/**
 * {
        "amount": -4.0,
        "currencyCode": null
    }
 */
export class Amount {
  [key: string]: unknown;
  amount?: number;
  currencyCode?: string | null;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): Amount {
    return new Amount(data);
  }
}
