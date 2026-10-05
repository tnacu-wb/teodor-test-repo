import { FORM_VALIDATIONS } from './formConstants';

describe('postal code patterns', () => {
  it.each(['SW1A 1AA', 'EC1A 1BB', 'M1 1AE', 'W1A0AX', 'GIR 0AA'])(
    'accepts UK postcode %s',
    (postcode) => {
      expect(FORM_VALIDATIONS.POSTAL_CODE.MATCHES.test(postcode)).toBe(true);
    }
  );

  it.each(['10115', '01067', '80331'])(
    'accepts German postcode %s, including leading zeroes',
    (postcode) => {
      expect(FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE.test(postcode)).toBe(true);
    }
  );

  it.each(['-', '.', '^', '/', '[', 'A', '0'])(
    'rejects an extra trailing character %s',
    (suffix) => {
      expect(FORM_VALIDATIONS.POSTAL_CODE.MATCHES.test(`SW1A 1AA${suffix}`)).toBe(false);
      expect(FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE.test(`10115${suffix}`)).toBe(false);
    }
  );
});
