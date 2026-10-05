import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';

export const getSavedPackagesOpera = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'hotelId', value: args.hotelId, required: false },
      { key: 'basketReferenceId', value: args.basketReferenceId, required: false },
      { key: 'mealInclusiveRate', value: args.showMealInclusiveRate, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.GET_SAVED_PACKAGES_OPERA, getSavedPackagesOpera, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};
