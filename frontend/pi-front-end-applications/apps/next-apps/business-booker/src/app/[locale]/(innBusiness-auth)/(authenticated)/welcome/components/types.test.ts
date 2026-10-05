import { ConfirmationStep } from './types';

describe('ConfirmationStep Enum', () => {
  it('should have a CONFIRMATION value', () => {
    expect(ConfirmationStep.CONFIRMATION).toBe('CONFIRMATION');
  });

  it('should not have undefined values', () => {
    Object.values(ConfirmationStep).forEach((value) => {
      expect(value).toBeDefined();
    });
  });

  it('should only contain string values', () => {
    Object.values(ConfirmationStep).forEach((value) => {
      expect(typeof value).toBe('string');
    });
  });
});
