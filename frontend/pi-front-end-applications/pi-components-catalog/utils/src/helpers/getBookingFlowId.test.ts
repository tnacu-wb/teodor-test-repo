import { getBookingFlowId } from './getBookingFlowId';

describe('getBookingFlowId', () => {
  it('should return "booking-business" for PI brand', () => {
    const result = getBookingFlowId('PI');
    expect(result).toBe('booking-business');
  });

  it('should return "booking-business" for ZIP brand', () => {
    const result = getBookingFlowId('ZIP');
    expect(result).toBe('booking-business');
  });

  it('should return "booking-business-ct" for PID brand', () => {
    const result = getBookingFlowId('PID');
    expect(result).toBe('booking-business-ct');
  });

  it('should return "booking-business-hub" for HUB brand', () => {
    const result = getBookingFlowId('HUB');
    expect(result).toBe('booking-business-hub');
  });

  it('should return default "booking-business" for unknown brand', () => {
    const result = getBookingFlowId('UNKNOWN');
    expect(result).toBe('booking-business');
  });

  it('should return default "booking-business" for empty string', () => {
    const result = getBookingFlowId('');
    expect(result).toBe('booking-business');
  });

  it('should return default "booking-business" for null/undefined (type coercion)', () => {
    const result = getBookingFlowId(null as unknown as string);
    expect(result).toBe('booking-business');
  });

  it('should handle lowercase brand names correctly', () => {
    expect(getBookingFlowId('pi')).toBe('booking-business');
    expect(getBookingFlowId('pid')).toBe('booking-business');
    expect(getBookingFlowId('zip')).toBe('booking-business');
    expect(getBookingFlowId('hub')).toBe('booking-business');
  });

  it('should handle mixed case brand names', () => {
    expect(getBookingFlowId('Pi')).toBe('booking-business');
    expect(getBookingFlowId('Pid')).toBe('booking-business');
    expect(getBookingFlowId('Hub')).toBe('booking-business');
  });
});
