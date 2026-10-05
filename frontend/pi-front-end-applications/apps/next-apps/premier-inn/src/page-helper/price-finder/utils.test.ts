import { extractPriceFinderPath } from './utils';

describe('extractPriceFinderPath', () => {
  it('should extract path from /price-finder/ URL', () => {
    expect(extractPriceFinderPath('/gb/en/price-finder/london-hotels')).toBe('london-hotels');
  });

  it('should extract path from /calendar/ URL', () => {
    expect(extractPriceFinderPath('/gb/en/calendar/manchester-hotels')).toBe('manchester-hotels');
  });

  it('should extract path from /kalender/ URL', () => {
    expect(extractPriceFinderPath('/de/de/kalender/berlin-hotels')).toBe('berlin-hotels');
  });

  it('should split and remove query params from path', () => {
    expect(extractPriceFinderPath('/price-finder/test?foo=bar')).toBe('test');
  });

  it('should split and remove hash from path', () => {
    expect(extractPriceFinderPath('/calendar/test#section')).toBe('test');
  });

  it('should replace trailing slash from path', () => {
    expect(extractPriceFinderPath('/kalender/test/')).toBe('test');
  });

  it('should replace .html extension from path', () => {
    expect(extractPriceFinderPath('/price-finder/test.html')).toBe('test');
  });

  it('should handle all path cleaning together', () => {
    expect(extractPriceFinderPath('/price-finder/test.html/?foo=bar#section')).toBe('test');
  });

  it('should return empty string when no match found', () => {
    expect(extractPriceFinderPath('/gb/en/hotels')).toBe('');
    expect(extractPriceFinderPath('')).toBe('');
    expect(extractPriceFinderPath('/price-finder/')).toBe('');
  });
});
