import { Page, Locator } from '@playwright/test';

export const locateByPartialTestId = (container: Page | Locator, id: string) => {
  return container.locator(`[data-testid*="${id}"]`);
};

async function navigateToMonth(
  page: Page,
  targetMonth: string,
  targetYear: number,
  locale: string
) {
  const targetMonthYear = `${targetMonth} ${targetYear}`;

  const currentMonthSelected = new Date().toLocaleString(locale, { month: 'long' });
  const currentYearSelected = new Date().getFullYear();
  let currentMonthYearSelected = `${currentMonthSelected} ${currentYearSelected}`;

  while (currentMonthYearSelected !== targetMonthYear) {
    const nextMonth = '[name="next-month"]';
    await page.click(nextMonth);

    const newCurrentMonthYearSelectedText = await page.textContent('div[role="presentation"]');
    if (newCurrentMonthYearSelectedText !== null) {
      currentMonthYearSelected = newCurrentMonthYearSelectedText;
    } else {
      break;
    }
  }
}

async function selectDay(page: Page, day: number) {
  await page.getByRole('gridcell', { name: `${day}`, exact: true }).click();
}

export async function selectDateRange(page: Page, startDate: Date, endDate: Date, locale: string) {
  const startDay = startDate.getDate();
  const startMonth = startDate.toLocaleString(locale, { month: 'long' });
  const startYear = startDate.getFullYear();

  const endDay = endDate.getDate();
  const endMonth = endDate.toLocaleString(locale, { month: 'long' });
  const endYear = endDate.getFullYear();

  await navigateToMonth(page, startMonth, startYear, locale);

  await selectDay(page, startDay);

  if (startMonth !== endMonth || startYear !== endYear) {
    await navigateToMonth(page, endMonth, endYear, locale);
  }

  await selectDay(page, endDay);
}
