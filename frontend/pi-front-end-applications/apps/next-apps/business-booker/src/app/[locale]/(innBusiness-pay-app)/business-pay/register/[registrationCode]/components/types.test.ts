import { RegisterIbStep } from './types';

describe('RegisterIbStep Enum', () => {
  it('should have a QUESTIONS_FORM value', () => {
    expect(RegisterIbStep.QUESTIONS_FORM).toBe('QUESTIONS_FORM');
  });

  it('should have a DETAILS_FORM value', () => {
    expect(RegisterIbStep.DETAILS_FORM).toBe('DETAILS_FORM');
  });

  it('should not have undefined values', () => {
    Object.values(RegisterIbStep).forEach((value) => {
      expect(value).toBeDefined();
    });
  });

  it('should have exactly two keys', () => {
    expect(Object.keys(RegisterIbStep).length).toBe(2);
  });

  it('should match the expected enum structure', () => {
    const expectedEnum = {
      QUESTIONS_FORM: 'QUESTIONS_FORM',
      DETAILS_FORM: 'DETAILS_FORM',
    };
    expect(RegisterIbStep).toEqual(expectedEnum);
  });
});
