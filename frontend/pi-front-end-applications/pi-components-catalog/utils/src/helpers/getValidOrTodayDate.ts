export function getValidOrTodayDate(
  ARRdd: string | string[] | undefined,
  ARRmm: string | string[] | undefined,
  ARRyyyy: string | string[] | undefined,
  today: string
): string {
  const day = Number(Array.isArray(ARRdd) ? ARRdd[0] : ARRdd);
  const month = Number(Array.isArray(ARRmm) ? ARRmm[0] : ARRmm);
  const year = Number(Array.isArray(ARRyyyy) ? ARRyyyy[0] : ARRyyyy);

  if (Number.isNaN(day) || Number.isNaN(month) || Number.isNaN(year)) {
    return today;
  }

  const inputDate = new Date(year, month - 1, day);
  const currentDate = new Date();

  inputDate.setHours(0, 0, 0, 0);
  currentDate.setHours(0, 0, 0, 0);

  const isValidDate =
    inputDate.getFullYear() === year &&
    inputDate.getMonth() === month - 1 &&
    inputDate.getDate() === day;

  const isInFutureOrToday = inputDate >= currentDate;

  return isValidDate && isInFutureOrToday ? inputDate.toLocaleDateString('en-CA') : today;
}
