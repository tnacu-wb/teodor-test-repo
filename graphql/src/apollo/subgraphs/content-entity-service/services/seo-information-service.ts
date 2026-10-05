import { SeoInformationCriteria } from '../models/seo-information-criteria';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

export const getSeoInformation = async (
  seoInformationCriteria: SeoInformationCriteria,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'page', value: seoInformationCriteria.page, required: true },
      { key: 'hotelId', value: seoInformationCriteria.hotelId, required: false },
      { key: 'language', value: seoInformationCriteria.language, required: true },
      { key: 'country', value: seoInformationCriteria.country, required: true },
      { key: 'bookingFlowId', value: seoInformationCriteria.bookingFlowId, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.SEO_INFORMATION, getSeoInformation, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
