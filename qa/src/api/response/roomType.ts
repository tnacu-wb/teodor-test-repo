import { Room } from './room';

/** API model for the current TypeScript test framework. */
export class RoomType {
  [key: string]: unknown;
  adults?: number;
  children?: number;
  roomType?: string;
  rooms: Room[] = [];

  /**
   * Room Type constructor
   * @param data object data
   * @param data.roomTypeApiResponse response from API
   */
  constructor(data: { roomTypeApiResponse?: Record<string, unknown> } = {}) {
    const roomTypeApiResponse = data.roomTypeApiResponse ?? {};
    this.roomType = roomTypeApiResponse.roomType as string | undefined;
    this.adults = roomTypeApiResponse.adults as number | undefined;
    this.children = roomTypeApiResponse.children as number | undefined;
    const rooms = Array.isArray(roomTypeApiResponse.rooms) ? (roomTypeApiResponse.rooms as Array<Record<string, unknown>>) : [];
    this.rooms = rooms.map((room) => new Room({ roomApiResponse: room }));
  }

  static fromResponse(data: { roomTypeApiResponse?: Record<string, unknown> }): RoomType {
    return new RoomType(data);
  }

}
