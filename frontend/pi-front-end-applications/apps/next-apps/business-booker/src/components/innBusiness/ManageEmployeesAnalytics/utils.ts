export const getAnalyticsPageName = (searchParams?: URLSearchParams | null) => {
  const mainPageName = 'Manage Employees';
  let pageName = mainPageName;

  const pageNameWithSearch = `${mainPageName}: Search Employee`;

  if (searchParams?.get('userSearch')) {
    pageName = pageNameWithSearch;
  }

  return pageName;
};
