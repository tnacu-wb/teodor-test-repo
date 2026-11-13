/**
 * Date formatting and calculation utilities for test assertions.
 */

import { getCurrentLocale } from '../test-data/locales';

/**
 * Format an ISO date string to UI display format (e.g. "15 Jan").
 *
 * @param isoDate - Date in YYYY-MM-DD format
 * @returns Formatted date string (e.g. "15 Jan")
 */
export function formatDateForUI(isoDate: string): string {
  const date = new Date(isoDate + 'T12:00:00');
  const day = date.getDate();
  const locale = getCurrentLocale();
  const localeCode = `${locale.language}-${locale.country.toUpperCase()}`;
  const month = date.toLocaleString(localeCode, { month: 'short' }).replace('Sept', 'Sep');
  return `${day} ${month}`;
}

/**
 * Format an ISO date string to long display format (e.g. "15 January 2025").
 *
 * @param isoDate - Date in YYYY-MM-DD format
 * @returns Formatted date string (e.g. "15 January 2025")
 */
export function formatDateLong(isoDate: string): string {
  const date = new Date(isoDate + 'T12:00:00');
  return date.toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' });
}

/**
 * Get a future date string offset from today.
 *
 * @param daysFromNow - Number of days from today
 * @returns ISO date string (YYYY-MM-DD)
 */
export function futureDate(daysFromNow: number): string {
  const date = new Date();
  date.setDate(date.getDate() + daysFromNow);
  return date.toISOString().split('T')[0];
}

/**
 * Calculate the number of nights between two ISO date strings.
 *
 * @param checkIn - Check-in date (YYYY-MM-DD)
 * @param checkOut - Check-out date (YYYY-MM-DD)
 * @returns Number of nights
 */
export function calculateNights(checkIn: string, checkOut: string): number {
  const msPerDay = 86400000;
  const start = new Date(checkIn + 'T12:00:00').getTime();
  const end = new Date(checkOut + 'T12:00:00').getTime();
  return Math.round((end - start) / msPerDay);
}
