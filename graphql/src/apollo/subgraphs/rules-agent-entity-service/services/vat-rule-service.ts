import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';

/**
 * This method is used to get all the VAT rules
 * @param vatRegion The vat region
 * @param pkgCodeArr The package code array
 * @param context The context object
 * @returns The response containing VAT rule
 */
export const getVatRule = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const pkgCodeArr = (args.pkgCodeArr || []).join(',');
    const fieldToAdd = [
      { key: 'vatRegion', value: args.vatRegion, required: true },
      { key: 'pkgCodeArr', value: pkgCodeArr, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);
    console.log('finalMap', finalMap);
    return await get(endpoints.VAT_RULE, getVatRule, finalMap, context);
  } catch (error) {
    handleError(error, args);
  }
};
