/**
"accessibleRoom": {
    "phoneNumber": "+49 (0)69 8500691",
    "isAccessible": false
}
 */
export class AccessibleRoom {
  [key: string]: unknown;
  isAccessible?: boolean;
  phoneNumber?: string;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): AccessibleRoom {
    return new AccessibleRoom(data);
  }
}
