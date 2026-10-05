/**
 * "bookingSpinnerConfig": [
    {
      "order": "1",
      "seconds": "10",
      "text": "Einen Augenblick bitte…"
    },
    {
      "order": "2",
      "seconds": "20",
      "text": "Einen Augenblick bitte, wir bearbeiten Ihre Anfrage"
    },
    {
      "order": "3",
      "seconds": "30",
      "text": "Entschuldigen Sie die Verzögerung – bitte haben Sie noch etwas Geduld"
    }
  ]
 */
export class BookingSpinnerConfigItem {
  [key: string]: unknown;
  order?: string;
  seconds?: string;
  text?: string;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): BookingSpinnerConfigItem {
    return new BookingSpinnerConfigItem(data);
  }
}
