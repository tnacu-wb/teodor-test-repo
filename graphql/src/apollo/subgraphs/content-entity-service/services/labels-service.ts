import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import {
  addFieldsToMap,
  replaceServiceEndpoint,
  transformToJsonResponse
} from '../../../utils/base-utils';
import { CategoryLabelsRequest } from '../models/content-entity-models';

/**
 * This method is used to fetch the labels
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param headers The headers
 * @param info The info
 * @returns The response containing labels list
 */
export const getLabels = async (args: any, context: any, info: any): Promise<any> => {
  try {
    const categoryMap: { [key: string]: string } = {
      main: 'main',
      booking: 'booking',
      piBookings: 'pi_bookings',
      piPreCheckIn: 'pi_pre_checkin',
      piGroupBooking: 'pi_group_booking',
      extras: 'extras',
      promotions: 'promotions'
    };

    const selectionSetList: string[] = info.fieldNodes[0].selectionSet.selections.map(
      (selection: any) => selection.name.value
    );
    let categories = selectionSetList
      .filter((field) => categoryMap[field])
      .map((field) => categoryMap[field])
      .join(', ');

    const params = {
      categories: categories,
      language: args.language,
      country: args.country
    };
    const response = await get(endpoints.LABELS, getLabels, params, context);

    return transformToJsonResponse(response);
  } catch (error) {
    handleError(error, args);
  }
};

/**
 * This method is used to fetch the labels
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param headers The headers
 * @param info The info
 * @returns The response containing labels list
 */
export const getCategoryLabels = async (
  categoryLabelsRequest: CategoryLabelsRequest,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const labelsString = categoryLabelsRequest?.labels?.join(',') || '';

    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.CATEGORY_LABELS,
      '{category}',
      categoryLabelsRequest.category
    );

    const fieldsToAdd = [
      { key: 'language', value: categoryLabelsRequest?.language, required: true },
      { key: 'country', value: categoryLabelsRequest?.country, required: true },
      { key: 'labels', value: labelsString, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    const response = await get(serviceEndpoint, getCategoryLabels, finalMap, context);

    const serializedLabels = JSON.stringify(JSON.stringify(response));

    return { labels: serializedLabels };
  } catch (error) {
    handleError(error, finalMap);
  }
};
