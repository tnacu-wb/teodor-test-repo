import { getAnalyticsPagesMap, AnalyticsPageDetails } from '@whitbread-eos/api';

import analytics from './analytics';

export const formatAnalyticsFunnelStep = (
  siteType: 'PI' | 'PIB' | 'CCUI',
  page: string,
  language?: string
) => {
  const countryByLanguage = language?.toLocaleLowerCase() === 'de' ? 'DE' : 'UK';

  const formattedSiteType = siteType === 'PIB' ? 'PB' : siteType;

  return `Web:${formattedSiteType}:${countryByLanguage}:${page}`;
};

export default function setPageAnalytics(
  pathName: string,
  siteType: 'PI' | 'PIB' | 'CCUI',
  queryParams: Record<string, string | string[] | undefined>,
  language?: string
) {
  const pagesMap = getAnalyticsPagesMap(siteType);
  const pageDetails = pagesMap.find((pageDetails: AnalyticsPageDetails) =>
    pathName.includes(pageDetails.pathMatch)
  );

  if (!pageDetails) return;

  const page = pageDetails.page === 'HDP' && !queryParams.NIGHTS ? 'DLP' : pageDetails.page;

  const funnelStep = formatAnalyticsFunnelStep(siteType, page, language);

  analytics.update({
    pageName:
      page === 'AddToWallet' && language === 'de'
        ? 'Web:PI:DE:QR Add to Wallet Apple'
        : pageDetails.pageName,
    pageType: pageDetails.pageType,
    siteType: siteType,
    funnel_step: funnelStep,
  });
}
