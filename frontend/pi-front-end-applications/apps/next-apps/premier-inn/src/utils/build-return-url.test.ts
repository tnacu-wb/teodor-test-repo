import fc from 'fast-check';

import { buildReturnUrl } from './build-return-url';

/**
 * **Validates: Requirement 3.5**
 *
 * Property: returnUrl always equals current URL + correct `source=datatrans` separator
 * for any { path, existingParams }.
 */
describe('buildReturnUrl — Property-Based Tests', () => {
  it('should always contain the original URL unchanged at the start', () => {
    fc.assert(
      fc.property(fc.webUrl({ withQueryParameters: true }), (url) => {
        const result = buildReturnUrl(url);
        expect(result.startsWith(url)).toBe(true);
      }),
      { numRuns: 5 }
    );
  });

  it('should always end with source=datatrans', () => {
    fc.assert(
      fc.property(fc.webUrl({ withQueryParameters: true }), (url) => {
        const result = buildReturnUrl(url);
        expect(result.endsWith('source=datatrans')).toBe(true);
      }),
      { numRuns: 5 }
    );
  });

  it('should use & separator when URL already has query params', () => {
    fc.assert(
      fc.property(
        fc.webUrl({ withQueryParameters: false }).map((base) => `${base}?existing=param`),
        (url) => {
          const result = buildReturnUrl(url);
          expect(result).toBe(`${url}&source=datatrans`);
        }
      ),
      { numRuns: 5 }
    );
  });

  it('should use ? separator when URL has no query params', () => {
    fc.assert(
      fc.property(
        fc.webUrl({ withQueryParameters: false }).filter((u) => !u.includes('?')),
        (url) => {
          const result = buildReturnUrl(url);
          expect(result).toBe(`${url}?source=datatrans`);
        }
      ),
      { numRuns: 5 }
    );
  });

  it('should append with & when URL has ? anywhere in the string', () => {
    fc.assert(
      fc.property(
        fc.record({
          path: fc.stringMatching(/^[a-z\-_\/]{1,20}$/),
          paramKey: fc.stringMatching(/^[a-z]{1,5}$/),
          paramValue: fc.stringMatching(/^[a-z0-9]{1,5}$/),
        }),
        ({ path, paramKey, paramValue }) => {
          const url = `https://premierinn.com/${path}?${paramKey}=${paramValue}`;
          const result = buildReturnUrl(url);

          // Should use & separator since ? already exists
          expect(result).toBe(`${url}&source=datatrans`);
          // Result should contain original URL
          expect(result).toContain(url);
          // Result should contain source=datatrans
          expect(result).toContain('source=datatrans');
        }
      ),
      { numRuns: 5 }
    );
  });

  it('should construct correct returnUrl for path-only URLs', () => {
    fc.assert(
      fc.property(fc.stringMatching(/^[a-z\-_\/]{1,20}$/), (path) => {
        const url = `https://premierinn.com/${path}`;
        const result = buildReturnUrl(url);

        // Should use ? separator since no existing query
        expect(result).toBe(`${url}?source=datatrans`);
      }),
      { numRuns: 5 }
    );
  });
});
