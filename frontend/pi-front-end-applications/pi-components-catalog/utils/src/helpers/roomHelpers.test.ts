import { ROOM_CODES, Room } from '@whitbread-eos/api';

import {
  hasSameRoomTypeCodes,
  validateRoomOccupancyConditions,
  roomDefaultValueIfError,
  isInnBusinessApp,
} from './roomHelpers';

describe('hasSameRoomTypeCodes', () => {
  it('should return true for equivalent room type code sets after normalization', () => {
    expect(hasSameRoomTypeCodes(' a , b , a ', ['B', 'A'])).toBe(true);
    expect(hasSameRoomTypeCodes([' c ', 'd', ''], 'D,C')).toBe(true);
  });

  it('should return false when left or right input is empty', () => {
    expect(hasSameRoomTypeCodes('', 'A')).toBe(false);
    expect(hasSameRoomTypeCodes(null, ['A'])).toBe(false);
    expect(hasSameRoomTypeCodes('A,A', 'A,B')).toBe(false);
  });

  it('should return false when normalized code sets have different members', () => {
    expect(hasSameRoomTypeCodes('A,B', 'A,C')).toBe(false);
  });

  it('should return true when right is a proper subset of left', () => {
    expect(hasSameRoomTypeCodes('A,B,C', 'A')).toBe(true);
  });

  it('should return true when right is a subset with multiple codes', () => {
    expect(hasSameRoomTypeCodes(['A', 'B', 'C'], ['A', 'B'])).toBe(true);
  });

  it('should return false when right has codes not in left', () => {
    expect(hasSameRoomTypeCodes('A,B', 'A,B,C')).toBe(false);
  });

  it('should return false for empty right with non-empty left', () => {
    expect(hasSameRoomTypeCodes('A', '')).toBe(false);
  });
});

describe('validateRoomOccupancyConditions', () => {
  describe('valid inputs', () => {
    it('should return false for valid double room with 1 adult', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, '0')).toBe(false);
    });

    it('should return false for valid double room with 2 adults', () => {
      expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.double, '0')).toBe(false);
    });

    it('should return false for valid family room', () => {
      expect(validateRoomOccupancyConditions('2', '1', ROOM_CODES.family, '0')).toBe(false);
      expect(validateRoomOccupancyConditions('2', '2', ROOM_CODES.family, '0')).toBe(false);
      expect(validateRoomOccupancyConditions('1', '1', ROOM_CODES.family, '0')).toBe(false);
    });

    it('should return false for valid twin room', () => {
      expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.twin, '0')).toBe(false);
    });

    it('should return false for valid accessible room', () => {
      expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.accessible, '0')).toBe(false);
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.accessible, '0')).toBe(false);
    });

    it('should return false for valid single room with 1 adult', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.single, '0')).toBe(false);
    });

    it('should return false when cot is not provided', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double)).toBe(false);
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, null)).toBe(false);
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, undefined)).toBe(false);
    });

    it('should return false for valid cot value of 1', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, '1')).toBe(false);
    });
  });

  describe('invalid adult count', () => {
    it('should return true for 0 adults', () => {
      expect(validateRoomOccupancyConditions('0', '0', ROOM_CODES.double, '0')).toBe(true);
    });

    it('should return true for more than 2 adults', () => {
      expect(validateRoomOccupancyConditions('3', '0', ROOM_CODES.double, '0')).toBe(true);
      expect(validateRoomOccupancyConditions('3', '2', ROOM_CODES.family, '0')).toBe(true);
      expect(validateRoomOccupancyConditions('3', '0', ROOM_CODES.twin, '0')).toBe(true);
      expect(validateRoomOccupancyConditions('3', '0', ROOM_CODES.accessible, '0')).toBe(true);
    });

    it('should return true for 2 adults in single room', () => {
      expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.single, '0')).toBe(true);
    });

    it('should return true for 1 adult in twin room', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.twin, '0')).toBe(true);
    });

    it('should return true for NaN adult value', () => {
      expect(validateRoomOccupancyConditions('abc', '0', ROOM_CODES.double, '0')).toBe(true);
      expect(validateRoomOccupancyConditions('', '0', ROOM_CODES.double, '0')).toBe(true);
    });

    it('should return true for negative adult count', () => {
      expect(validateRoomOccupancyConditions('-1', '0', ROOM_CODES.double, '0')).toBe(true);
    });
  });

  describe('invalid child count', () => {
    it('should return true for more than 2 children', () => {
      expect(validateRoomOccupancyConditions('2', '3', ROOM_CODES.family, '0')).toBe(true);
    });

    it('should return true for negative children count', () => {
      expect(validateRoomOccupancyConditions('2', '-1', ROOM_CODES.family, '0')).toBe(true);
    });

    it('should return true for NaN child value', () => {
      expect(validateRoomOccupancyConditions('2', 'abc', ROOM_CODES.family, '0')).toBe(true);
    });

    it('should return true for 0 children in family room', () => {
      expect(validateRoomOccupancyConditions('2', '0', ROOM_CODES.family, '0')).toBe(true);
    });

    it('should return true for children in non-family room', () => {
      expect(validateRoomOccupancyConditions('2', '1', ROOM_CODES.double, '0')).toBe(true);
      expect(validateRoomOccupancyConditions('1', '1', ROOM_CODES.single, '0')).toBe(true);
      expect(validateRoomOccupancyConditions('2', '1', ROOM_CODES.twin, '0')).toBe(true);
    });
  });

  describe('invalid cot values', () => {
    it('should return true for negative cot value', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, '-1')).toBe(true);
    });

    it('should return true for cot value greater than 1', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, '2')).toBe(true);
    });

    it('should return true for NaN cot value', () => {
      expect(validateRoomOccupancyConditions('1', '0', ROOM_CODES.double, 'abc')).toBe(true);
    });
  });

  describe('invalid room type', () => {
    it('should return true for invalid room type', () => {
      expect(validateRoomOccupancyConditions('1', '0', 'INVALID', '0')).toBe(true);
      expect(validateRoomOccupancyConditions('1', '0', '', '0')).toBe(true);
      expect(validateRoomOccupancyConditions('1', '0', null, '0')).toBe(true);
    });
  });
});

describe('roomDefaultValueIfError', () => {
  const defaultRoom = [{ adultsNumber: 1, childrenNumber: 0, type: ROOM_CODES.double }];

  it('should return default values when room array is empty', () => {
    const rooms: Room[] = [];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(defaultRoom);
  });

  it('should return default values when room has invalid adult count', () => {
    const rooms = [{ adultsNumber: 3, childrenNumber: 0, type: ROOM_CODES.double }];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(defaultRoom);
  });

  it('should return default values when any room in array is invalid', () => {
    const rooms = [
      { adultsNumber: 2, childrenNumber: 0, type: ROOM_CODES.double },
      { adultsNumber: 2, childrenNumber: 0, type: ROOM_CODES.single }, // Invalid: 2 adults in single
    ];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(defaultRoom);
  });

  it('should return original rooms when all values are valid', () => {
    const rooms = [
      { adultsNumber: 2, childrenNumber: 0, type: ROOM_CODES.double },
      { adultsNumber: 1, childrenNumber: 1, type: ROOM_CODES.family },
    ];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(rooms);
  });

  it('should return original rooms for single valid room', () => {
    const rooms = [{ adultsNumber: 1, childrenNumber: 0, type: ROOM_CODES.single }];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(rooms);
  });

  it('should handle room with undefined type by returning default', () => {
    const rooms = [{ adultsNumber: 1, childrenNumber: 0, type: undefined }] as Room[];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(defaultRoom);
  });

  it('should handle room with null type by returning default', () => {
    const rooms = [{ adultsNumber: 1, childrenNumber: 0, type: null }] as unknown as Room[];

    const result = roomDefaultValueIfError(rooms);

    expect(result).toEqual(defaultRoom);
  });
});

describe('isInnBusinessApp', () => {
  const originalNodeEnv = process.env.NODE_ENV;

  afterEach(() => {
    process.env.NODE_ENV = originalNodeEnv;
  });

  it('should return true when NODE_ENV is development', () => {
    process.env.NODE_ENV = 'development';

    const result = isInnBusinessApp('any-hostname');

    expect(result).toBe(true);
  });

  it('should return true when NODE_ENV is development even for non-business hostname', () => {
    process.env.NODE_ENV = 'development';

    const result = isInnBusinessApp('premier-inn.com');

    expect(result).toBe(true);
  });

  it('should return true when hostname contains "business" in production', () => {
    process.env.NODE_ENV = 'production';

    expect(isInnBusinessApp('innbusiness.com')).toBe(true);
    expect(isInnBusinessApp('business.premierinn.com')).toBe(true);
    expect(isInnBusinessApp('www.business-booker.com')).toBe(true);
  });

  it('should return false when hostname does not contain "business" in production', () => {
    process.env.NODE_ENV = 'production';

    expect(isInnBusinessApp('premierinn.com')).toBe(false);
    expect(isInnBusinessApp('www.premierinn.com')).toBe(false);
    expect(isInnBusinessApp('localhost')).toBe(false);
  });

  it('should return falsy value for empty hostname in production', () => {
    process.env.NODE_ENV = 'production';

    expect(isInnBusinessApp('')).toBeFalsy();
  });

  it('should handle test environment like production', () => {
    process.env.NODE_ENV = 'test';

    expect(isInnBusinessApp('business.premierinn.com')).toBe(true);
    expect(isInnBusinessApp('premierinn.com')).toBe(false);
  });
});
