export class RoomSearchV2 {
  tag: string;
  adults: number;
  children: number;
  numberOfRooms: number;
  pmsRoomType?: string;

  constructor(data: any) {
    this.tag = data.tag;
    this.adults = data.adults;
    this.children = data.children;
    this.numberOfRooms = data.numberOfRooms;
    this.pmsRoomType = data.pmsRoomType;
  }
}
