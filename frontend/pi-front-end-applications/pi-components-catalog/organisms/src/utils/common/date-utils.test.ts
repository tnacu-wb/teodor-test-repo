import { generateMonthData, type MonthData } from './date-utils';

describe('date-utils', () => {
  beforeEach(() => {
    jest.useRealTimers();
  });

  describe('generateMonthData', () => {
    it('should generate 12 months by default', () => {
      const months = generateMonthData();
      expect(months).toHaveLength(12);
    });

    it('should generate correct number of months when maxMonths is specified', () => {
      const months = generateMonthData(6);
      expect(months).toHaveLength(6);
    });

    it('should have correct value format (YYYY-MM)', () => {
      const months = generateMonthData(1);
      const valueRegex = /^\d{4}-\d{2}$/;
      expect(months[0].value).toMatch(valueRegex);
    });

    it('should increment price by 10 for each month', () => {
      const months = generateMonthData(3);
      expect(months[0].price).toBe(80);
      expect(months[1].price).toBe(90);
      expect(months[2].price).toBe(100);
    });

    it('should handle year change correctly', () => {
      // Simulate December
      jest.useFakeTimers().setSystemTime(new Date(Date.UTC(2023, 11, 1)));
      const months = generateMonthData(2);
      expect(months[0].value.startsWith('2023')).toBe(true);
      expect(months[1].value.startsWith('2024')).toBe(true);
      jest.useRealTimers();
    });

    it('should use correct locale for month label', () => {
      const monthsDE = generateMonthData(1, 'de-DE');
      // Check that the German label contains a known German month name
      const germanMonths = [
        'Januar',
        'Februar',
        'März',
        'April',
        'Mai',
        'Juni',
        'Juli',
        'August',
        'September',
        'Oktober',
        'November',
        'Dezember',
      ];
      expect(germanMonths).toContain(monthsDE[0].label.replace(/'.*/, ''));
    });

    it('should add year suffix for next year', () => {
      // Simulate December
      jest.useFakeTimers().setSystemTime(new Date(Date.UTC(2023, 11, 1)));
      const months = generateMonthData(13);
      expect(months[12].label).toMatch(/'\d{2}$/);
      jest.useRealTimers();
    });
  });

  describe('MonthData', () => {
    it('should have value, label, date, and price properties', () => {
      const month: MonthData = {
        value: '2023-12',
        label: 'December',
        date: new Date(),
        price: 100,
      };
      expect(month.value).toBeDefined();
      expect(month.label).toBeDefined();
      expect(month.date instanceof Date).toBe(true);
      expect(month.price).toBeDefined();
    });
  });
});
