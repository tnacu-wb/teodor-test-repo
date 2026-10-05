import { DEFAULT_NUMBER, ROOM_CODES, Room } from '@whitbread-eos/api';

/**
 * Validates room occupancy conditions.
 * @param adult - Number of adults as string
 * @param child - Number of children as string
 * @param roomType - Type of room
 * @param cot - Number of cots as string (optional)
 * @returns True if validation fails, false if valid
 */
export function validateRoomOccupancyConditions(
  adult: string | null,
  child: string | null,
  roomType: string | null,
  cot?: string | null
) {
  return (
    Number.isNaN(Number(adult)) ||
    Number(adult) < DEFAULT_NUMBER ||
    Number(adult) > 2 ||
    (Number(adult) > DEFAULT_NUMBER && roomType === ROOM_CODES.single) ||
    (Number(adult) === DEFAULT_NUMBER && roomType === ROOM_CODES.twin) ||
    Number.isNaN(Number(child)) ||
    Number(child) < 0 ||
    Number(child) > 2 ||
    (Number(child) === 0 && roomType === ROOM_CODES.family) ||
    (Number(child) > 0 && roomType !== ROOM_CODES.family) ||
    (cot !== null &&
      cot !== undefined &&
      (Number.isNaN(Number(cot)) || Number(cot) < 0 || Number(cot) > DEFAULT_NUMBER)) ||
    (roomType !== ROOM_CODES.single &&
      roomType !== ROOM_CODES.double &&
      roomType !== ROOM_CODES.family &&
      roomType !== ROOM_CODES.twin &&
      roomType !== ROOM_CODES.accessible)
  );
}

const normaliseRoomTypeCodes = (value?: string | string[] | null) => {
  const codes = Array.isArray(value) ? value : (value?.split(',') ?? []);

  return [...new Set(codes.map((code) => code.trim().toUpperCase()).filter(Boolean))];
};

export const hasSameRoomTypeCodes = (
  left?: string | string[] | null,
  right?: string | string[] | null
) => {
  const leftCodes = normaliseRoomTypeCodes(left);
  const rightCodes = normaliseRoomTypeCodes(right);

  if (!leftCodes.length || !rightCodes.length) {
    return false;
  }

  return rightCodes.every((code) => leftCodes.includes(code));
};

/**
 * Returns a default room configuration if the provided rooms array is invalid.
 * This is a client-safe utility function that can be used in both client and server contexts.
 *
 * @param rooms - Array of room configurations to validate
 * @returns The original rooms array if valid, or a default single room configuration
 */
export function roomDefaultValueIfError(rooms: Room[]) {
  const defaultValue = {
    adultsNumber: DEFAULT_NUMBER,
    childrenNumber: 0,
    type: ROOM_CODES.double,
  };

  if (rooms.length === 0) {
    return [{ ...defaultValue }];
  }

  const hasInvalidValue = rooms.some((room) => {
    return validateRoomOccupancyConditions(
      room.adultsNumber.toString(),
      room.childrenNumber.toString(),
      room.type ?? ''
    );
  });

  if (hasInvalidValue) {
    return [{ ...defaultValue }];
  }
  return rooms;
}

/**
 * Checks if the current app is an Inn Business application.
 * This is a client-safe utility function.
 *
 * @param hostname - The hostname to check
 * @returns True if running in development or if hostname contains 'business'
 */
export const isInnBusinessApp = (hostname: string) => {
  if (process.env.NODE_ENV === 'development') {
    return true;
  }

  return hostname?.includes('business');
};
