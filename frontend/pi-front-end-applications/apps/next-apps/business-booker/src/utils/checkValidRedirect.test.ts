import { LOCALES } from '@whitbread-eos/api';

import checkValidRedirect from './checkValidRedirect';

describe('checkValidRedirect', () => {
  it('returns true for a valid locale path', () => {
    expect(checkValidRedirect(`/${LOCALES.EN}/spending`)).toBe(true);
  });

  it('returns false for empty string', () => {
    expect(checkValidRedirect('')).toBe(false);
  });

  it("returns false for string '' ", () => {
    expect(checkValidRedirect("''")).toBe(false);
  });

  it('returns false for http URLs', () => {
    expect(checkValidRedirect('http://malicious.com')).toBe(false);
    expect(checkValidRedirect('https://malicious.com')).toBe(false);
  });

  it('returns true for /gb/en/business-booker', () => {
    expect(checkValidRedirect('/gb/en/business-booker')).toBe(true);
  });

  it('returns true for /de/de/business-booker', () => {
    expect(checkValidRedirect('/de/de/business-booker')).toBe(true);
  });

  it('returns false if path does not start with a supported locale', () => {
    expect(checkValidRedirect('/fr-fr/spending')).toBe(false);
    expect(checkValidRedirect('/spending')).toBe(false);
  });

  it('returns true for all supported locales', () => {
    // e.g for /en-gb/ or /de-de/
    Object.values(LOCALES).forEach((loc) => {
      expect(checkValidRedirect(`/${loc}/something`)).toBe(true);
    });
  });
});
