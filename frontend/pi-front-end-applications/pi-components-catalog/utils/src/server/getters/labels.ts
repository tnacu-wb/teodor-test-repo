import {
  getFooterDetails,
  getInnBusinessHeader,
  Language,
  getCardManagementLabels as getCardManagementLabelsQuery,
  getUserManagementLabels as getUserManagementLabelsQuery,
  getInnBusinessLayoutLabels as getInnBusinessLayoutLabelsQuery,
  getProfileManagementLabelsQuery,
  getCompanyManagementLabelsQuery,
  getHomepageLabelsQuery,
  getSpendingLabelsQuery,
  getAuthLabelsQuery,
  getPayApplicationLabelsQuery,
  getNotificationsLabelsQuery,
  getContactUsLabelsQuery,
} from '@whitbread-eos/api';

import { executeContentGraphQLQuery } from '../gql';

export const getFooterLabels = async (language: Language) => {
  return await executeContentGraphQLQuery(
    getFooterDetails(),
    {
      language,
      country: language === 'en' ? 'gb' : 'de',
      site: 'inn-business',
    },
    (result: any) => result?.data?.footer
  );
};

export const getInnBusinessHeaderLabels = async (language: Language) => {
  return await executeContentGraphQLQuery(
    getInnBusinessHeader(),
    {
      language,
      country: language === 'en' ? 'gb' : 'de',
      businessBooker: true,
    },
    (result: any) => result?.data?.headerInformation?.innBusinessHeader
  );
};

export const getCardManagementLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getCardManagementLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.cardManagementEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getInnBusinessLayoutLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getInnBusinessLayoutLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.layoutEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getUserManagementLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getUserManagementLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.userManagementEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getProfileManagementLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getProfileManagementLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.profileManagementEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getCompanyManagementLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getCompanyManagementLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.companyManagementEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getHomepageLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getHomepageLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.homepageEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getSpendingLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getSpendingLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.spendingReportingEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getAuthLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getAuthLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.authEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getPayApplicationLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getPayApplicationLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.payApplicationEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getInitials = (
  firstName: string | undefined | null,
  lastName: string | undefined | null
): string => {
  return (firstName?.charAt(0)?.toUpperCase() || '') + (lastName?.charAt(0)?.toUpperCase() || '');
};

export const getNotificationsLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getNotificationsLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.notificationsEndpoint
  );
  return data ? JSON.parse(data) : null;
};

export const getContactUsLabels = async (language: Language) => {
  const data = await executeContentGraphQLQuery(
    getContactUsLabelsQuery(),
    {
      country: language === 'en' ? 'gb' : 'de',
      language,
    },
    (result: any) => result?.data?.getPageData?.innbContactUsEndpoint
  );
  return data ? JSON.parse(data) : null;
};
