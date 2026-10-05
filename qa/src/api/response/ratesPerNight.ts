/**
 * Response example
 * "ratesPerNight": 
 * [
         {
            "startDate": "2023-02-01",
            "cityTaxPerNight": "2",
            "pricePerNight": 999.0
         }
    ]
 */
export class RatesPerNight {
  [key: string]: unknown;
  cityTaxPerNight?: string;
  pricePerNight?: number;
  startDate?: string;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): RatesPerNight {
    return new RatesPerNight(data);
  }
}
