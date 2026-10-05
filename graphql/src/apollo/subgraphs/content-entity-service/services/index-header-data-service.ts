import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, isFieldRequested } from '../../../utils/base-utils';
import createLogger from '../../../log/logger';
import { basename } from 'path';

const log = createLogger(basename(__filename));

/**
 * This method is used to fetch the header information
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param businessBooker as boolean
 * @returns The response containing header information
 */
export const getInBusinessHeader = async (args: any, context: any, info: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    const fieldsToAdd = [
      { key: 'language', value: args.language, required: true },
      { key: 'country', value: args.country, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.IN_BUSINESS_HEADER, getInBusinessHeader, finalMap, context);
  } catch (error) {
    handleError(error, args);
  }
};

export const getHeaderInformation = async (args: any, context: any, info: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    const fieldsToAdd = [
      { key: 'language', value: args.language, required: true },
      { key: 'country', value: args.country, required: true },
      { key: 'businessBooker', value: args.businessBooker, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    const headerInformation = await get(
      endpoints.HEADER_INFORMATION,
      getHeaderInformation,
      finalMap,
      context
    );

    const baseResponse = {
      content: headerInformation?.content ?? null,
      form: headerInformation?.form ?? null,
      datePicker: headerInformation?.datePicker ?? null,
      results: headerInformation?.results ?? null,
      announcement: headerInformation?.announcement ?? null,
      config: headerInformation?.config ?? null,
      contactBanner: headerInformation?.contactBanner ?? null
    };

    if (isFieldRequested('innBusinessHeader', info)) {
      const innBusinessHeader = await getInBusinessHeader(args, context, info);
      return { ...baseResponse, innBusinessHeader: innBusinessHeader ?? null };
    }

    return baseResponse;
  } catch (error) {
    handleError(error, args);
  }
};
