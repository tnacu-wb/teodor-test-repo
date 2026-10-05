import { addFieldsToMap } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { PartialAddressCriteria } from '../models/partial-address-criteria';

/**
 * This method is used to fetch the addresses for a given postcode.
 *
 * @param partialAddressCriteria
 * @param context
 * @returns List of address for the given postcode.
 */
export const getPartialAddress = async (
  { partialAddressCriteria }: { partialAddressCriteria: PartialAddressCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'searchTerm', value: partialAddressCriteria.searchTerm, required: true },
      { key: 'countryCode', value: partialAddressCriteria.countryCode, required: false }
    ];
    addFieldsToMap(fieldToAdd, finalMap);
    return await get(endpoints.PARTIAL_ADDRESS, getPartialAddress, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, partialAddressCriteria);
  }
};

/**
 * This method is used to format the address
 *
 * @param identifier
 * @param context
 * @returns List of address for the given postcode.
 */
export const getFormattedAddress = async (
  { identifier }: { identifier: string },
  context: any
): Promise<any> => {
  try {
    const formatAddressEndpoint = endpoints.FORMAT_ADDRESS.endpoint.replace(
      '{identifier}',
      identifier
    );
    const serviceEndpoint = { ...endpoints.FORMAT_ADDRESS, endpoint: formatAddressEndpoint };
    return await get(serviceEndpoint, getFormattedAddress, {}, context);
  } catch (error: Error | any) {
    handleError(error, identifier);
  }
};
