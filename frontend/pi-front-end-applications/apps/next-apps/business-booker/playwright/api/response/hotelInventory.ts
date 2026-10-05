export type HotelInventory = {
  roomTypeInventories: RoomTypeInventory[];
};

export type RoomTypeInventory = {
  availableCount: number;
  code: string;
};

function createRoomTypeInventory(roomTypeInventoryData: any): RoomTypeInventory {
  return {
    availableCount: roomTypeInventoryData.availableCount,
    code: roomTypeInventoryData.code,
  };
}

export function createHotelInventory(hotelInventoryData: any): HotelInventory {
  return {
    roomTypeInventories: hotelInventoryData.roomTypeInventories.map((roomTypeInventory: any) =>
      createRoomTypeInventory(roomTypeInventory)
    ),
  };
}
