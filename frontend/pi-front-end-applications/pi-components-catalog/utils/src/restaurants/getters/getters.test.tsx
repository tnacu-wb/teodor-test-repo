import { formatDateToString, fetchFormattedDateValues } from './getters';

describe('format date to string', () => {
  it('should return formatted date string', () => {
    expect(formatDateToString('Thu, 28 Sep 2023 04:19:25 GMT')).toEqual('28 Sep 2023');
  });

  it('should format date with correct month abbreviations', () => {
    expect(formatDateToString('Wed, 15 Jan 2024 12:00:00 GMT')).toEqual('15 Jan 2024');
    expect(formatDateToString('Thu, 29 Feb 2024 12:00:00 GMT')).toEqual('29 Feb 2024');
    expect(formatDateToString('Sun, 31 Dec 2023 12:00:00 GMT')).toEqual('31 Dec 2023');
  });

  it('should handle single digit days correctly', () => {
    expect(formatDateToString('Mon, 01 Jan 2024 12:00:00 GMT')).toEqual('1 Jan 2024');
    expect(formatDateToString('Fri, 05 May 2023 12:00:00 GMT')).toEqual('5 May 2023');
  });
});

describe('fetchFormattedDateValues', () => {
  it('should return formatted date string as YYYY-MM-DD', () => {
    expect(fetchFormattedDateValues('Thu, 28 Sep 2023 04:19:25 GMT')).toEqual('2023-09-28');
  });

  it('should pad single digit months and days with leading zeros', () => {
    expect(fetchFormattedDateValues('Mon, 01 Jan 2024 12:00:00 GMT')).toEqual('2024-01-01');
    expect(fetchFormattedDateValues('Fri, 05 May 2023 12:00:00 GMT')).toEqual('2023-05-05');
  });

  it('should format date correctly for last day of month', () => {
    expect(fetchFormattedDateValues('Sun, 31 Dec 2023 12:00:00 GMT')).toEqual('2023-12-31');
  });

  it('should format date correctly for leap year', () => {
    expect(fetchFormattedDateValues('Thu, 29 Feb 2024 12:00:00 GMT')).toEqual('2024-02-29');
  });

  it('should handle various date inputs consistently', () => {
    expect(fetchFormattedDateValues('2024/06/15')).toEqual('2024-06-15');
    expect(fetchFormattedDateValues('2023-12-25')).toEqual('2023-12-25');
  });
});
