/**
 
"roomExtraInfo": {
    "roomDescription": " Unsere geräumigen Familienzimmer sind ideal für bis zu zwei Erwachsene und bis zu zwei Kinder (bis einschließlich 15 Jahre). In den meisten Zimmern finden Sie ein super komfortables Hypnos-Doppelbett sowie eine Schlafcouch und ein ausziehbares Bett. Bei ",
    "roomName": "Familie",
    "roomType": "FMTRPL"
}
 
 */
export class RoomExtraInfo {
  [key: string]: unknown;
  roomDescription?: string;
  roomName?: string;
  roomType?: string;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromResponse<T extends Record<string, unknown>>(data: T): RoomExtraInfo {
    return new RoomExtraInfo(data);
  }
}
