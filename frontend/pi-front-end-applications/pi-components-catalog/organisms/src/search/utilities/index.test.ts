import { getRoomsPlaceholder } from './index';

const summaryLabels = {
  adult: 'adult',
  adults: 'adults',
  child: 'child',
  children: 'children',
  room: 'room',
  rooms: 'rooms',
};

describe('Search utilities', () => {
  it('should return the correct room summary for 2 adults 1 room', function () {
    expect(
      getRoomsPlaceholder(
        [{ adults: 2, children: 0, roomType: 'DB', shouldIncludeCot: false }],
        summaryLabels
      )
    ).toEqual('2 adults, 1 room');
  });

  it('should return the correct room summary for 3 adults, 1 child, 2 rooms', function () {
    expect(
      getRoomsPlaceholder(
        [
          { adults: 2, children: 1, roomType: 'FAM', shouldIncludeCot: false },
          { adults: 1, children: 0, roomType: 'SB', shouldIncludeCot: false },
        ],
        summaryLabels
      )
    ).toEqual('3 adults, 1 child, 2 rooms');
  });
});
