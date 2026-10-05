import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import { transformToJsonResponse } from '../../../utils/base-utils';

export const getPageData = async (args: any, context: any, info: any): Promise<any> => {
  try {
    const dictionaryMap: { [key: string]: string } = {
      layoutEndpoint: 'LAYOUT_DICTIONARY',
      cardManagementEndpoint: 'CARD_MANAGEMENT_DICTIONARY',
      commonIconsEndpoint: 'COMMON_ICONS_DICTIONARY',
      userManagementEndpoint: 'USER_MANAGEMENT_DICTIONARY',
      profileManagementEndpoint: 'PROFILE_MANAGEMENT_DICTIONARY',
      companyManagementEndpoint: 'COMPANY_MANAGEMENT_DICTIONARY',
      homepageEndpoint: 'HOMEPAGE_DICTIONARY',
      spendingReportingEndpoint: 'SPENDING_REPORTING_DICTIONARY',
      payApplicationEndpoint: 'PAY_APPLICATION_DICTIONARY',
      authEndpoint: 'AUTH_DICTIONARY',
      notificationsEndpoint: 'NOTIFICATIONS_DICTIONARY',
      innbContactUsEndpoint: 'CONTACT_US_DICTIONARY'
    };

    const selectionSetList: string[] = (info.fieldNodes?.[0]?.selectionSet?.selections ?? []).map(
      (selection: any) => selection.name.value
    );

    const dictionaries = selectionSetList
      .map((field) => dictionaryMap[field])
      .filter((field) => field)
      .join(', ');

    const params = {
      dictionaries: dictionaries,
      language: args.language,
      country: args.country
    };

    const response = await get(endpoints.GET_PAGE_DATA, getPageData, params, context);

    return transformToJsonResponse(response);
  } catch (error) {
    handleError(error, args);
  }
};
