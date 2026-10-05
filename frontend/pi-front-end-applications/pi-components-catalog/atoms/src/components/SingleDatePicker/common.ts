export function getYears(startYear: number) {
  const currentYear = new Date().getFullYear();
  return Array.from(Array(currentYear - startYear + 1).keys()).map((year) => startYear + year);
}

export const years = getYears(new Date().getFullYear() - 100);
