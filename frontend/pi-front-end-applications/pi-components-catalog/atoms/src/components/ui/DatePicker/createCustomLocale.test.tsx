import { format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';

import { createCustomLocale } from './createCustomLocale';

const monthsEn = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
];
const weekdaysShortEn = ['MonX', 'TueX', 'WedX', 'ThuX', 'FriX', 'SatX', 'SunX'];

const MONDAY = new Date(2025, 7, 4);
const WEDNESDAY = new Date(2025, 7, 6);
const SATURDAY = new Date(2025, 7, 9);
const SUNDAY = new Date(2025, 7, 10);

describe('createCustomLocale weekday accessibility', () => {
  const localeEn = createCustomLocale(enGB, monthsEn, weekdaysShortEn);

  describe('wide width (screen-reader aria-labels) returns the full weekday name', () => {
    it.each([
      [MONDAY, 'Monday'],
      [WEDNESDAY, 'Wednesday'],
      [SATURDAY, 'Saturday'],
      [SUNDAY, 'Sunday'],
    ])('cccc (column-header aria-label) for %s -> %s', (date, expected) => {
      expect(format(date, 'cccc', { locale: localeEn })).toBe(expected);
    });

    it.each([
      [MONDAY, 'Monday'],
      [WEDNESDAY, 'Wednesday'],
      [SATURDAY, 'Saturday'],
      [SUNDAY, 'Sunday'],
    ])('EEEE (day-cell aria-label) for %s -> %s', (date, expected) => {
      expect(format(date, 'EEEE', { locale: localeEn })).toBe(expected);
    });
  });

  describe('short/abbreviated widths (visible text) keep the custom short labels', () => {
    it('EEE (footer "EEE dd MMM yy") keeps the custom short label', () => {
      expect(format(MONDAY, 'EEE', { locale: localeEn })).toBe('MonX');
      expect(format(SUNDAY, 'EEE', { locale: localeEn })).toBe('SunX');
    });

    it('cccccc (visible column header) keeps the custom short label', () => {
      expect(format(MONDAY, 'cccccc', { locale: localeEn })).toBe('MonX');
      expect(format(SATURDAY, 'cccccc', { locale: localeEn })).toBe('SatX');
    });
  });

  it('localizes the full weekday name for the German locale', () => {
    const localeDe = createCustomLocale(de, monthsEn, weekdaysShortEn);
    expect(format(MONDAY, 'cccc', { locale: localeDe })).toBe('Montag');
    expect(format(SUNDAY, 'cccc', { locale: localeDe })).toBe('Sonntag');

    expect(format(MONDAY, 'EEE', { locale: localeDe })).toBe('MonX');
  });
});
