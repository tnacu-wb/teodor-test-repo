/**
A basket item from get basket API response, example: 
                {
                    "details": null,
                    "sourceId": "46916",
                    "type": "STAY"
                }
 */
export class BasketItem {
  [key: string]: unknown;
  details?: string | null;
  sourceId?: string;
  type?: string;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): BasketItem {
    return new BasketItem(data);
  }
}
