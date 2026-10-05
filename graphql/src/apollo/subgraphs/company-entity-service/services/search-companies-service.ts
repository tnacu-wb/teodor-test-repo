import { endpoints } from './base-service';
import { SearchCompaniesCriteria } from '../models/SearchCompaniesCriteria';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import {
  addFieldIfNotUndefined,
  addFieldIfNotUndefinedAndRequired
} from '../../../utils/base-utils';

const log = createLogger(basename(__filename));

export const searchCompaniesByProfile = async (
  searchCompaniesCriteria: SearchCompaniesCriteria,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [
      { key: 'hotelId', value: searchCompaniesCriteria.hotelId, required: false },
      { key: 'arNumber', value: searchCompaniesCriteria.arNumber, required: false },
      {
        key: 'companyName',
        value: searchCompaniesCriteria.companyName
          ? encodeURIComponent(searchCompaniesCriteria.companyName)
          : '',
        required: false
      },
      { key: 'limit', value: searchCompaniesCriteria.limit, required: false }
    ];

    fieldsToAdd.forEach(({ key, value, required }) => {
      if (required) {
        addFieldIfNotUndefinedAndRequired(value, key, finalMap);
      } else {
        addFieldIfNotUndefined(value, key, finalMap);
      }
    });

    log.info(`Final map to search for companies by profile: ${JSON.stringify(finalMap)}`);

    return await get(
      endpoints.SEARCH_COMPANIES_PROFILE,
      searchCompaniesByProfile,
      finalMap,
      context
    );
  } catch (error) {
    handleError(error, finalMap);
  }
};

export const searchCompaniesByParams = async (
  searchCompaniesCriteria: SearchCompaniesCriteria,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [
      { key: 'companyName', value: searchCompaniesCriteria.companyName, required: false },
      { key: 'arNumber', value: searchCompaniesCriteria.arNumber, required: false },
      {
        key: 'negotiatedRateCompanies',
        value: searchCompaniesCriteria.negotiatedRateCompanies,
        required: false
      },
      { key: 'limit', value: searchCompaniesCriteria.limit, required: false },
      { key: 'offset', value: searchCompaniesCriteria.offset, required: false }
    ];

    fieldsToAdd.forEach(({ key, value, required }) => {
      if (required) {
        addFieldIfNotUndefinedAndRequired(value, key, finalMap);
      } else {
        addFieldIfNotUndefined(value, key, finalMap);
      }
    });

    return await get(endpoints.SEARCH_COMPANIES, searchCompaniesByParams, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

export const searchCompanies = async (
  { searchCompaniesCriteria }: { searchCompaniesCriteria: SearchCompaniesCriteria },
  context: any
): Promise<any> => {
  try {
    if (!searchCompaniesCriteria.negotiatedRateCompanies) {
      return await searchCompaniesByProfile(searchCompaniesCriteria, context);
    } else {
      return await searchCompaniesByParams(searchCompaniesCriteria, context);
    }
  } catch (error) {
    handleError(error, searchCompaniesCriteria);
  }
};
