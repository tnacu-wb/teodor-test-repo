export const getCurrentDate = (offsetDays = 0) => {
  const currentDate = new Date();
  currentDate.setDate(currentDate.getDate() + offsetDays);

  const day = currentDate.getDate();
  const month = currentDate.getMonth() + 1;
  const year = currentDate.getFullYear();

  return {
    day,
    month,
    year,
  };
};
