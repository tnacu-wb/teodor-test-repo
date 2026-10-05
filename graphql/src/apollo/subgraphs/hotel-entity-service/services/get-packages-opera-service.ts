import { addFieldsToMap, replaceServiceEndpoint } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../packages-pipeline/actions/ActionContextKeys';

export const getPackagesOpera = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.GET_PACKAGES_OPERA,
      '{hotelId}',
      args.hotelId
    );

    let savedPackagesOpera = pipelineContext.get(ActionContextKeys.GET_SAVED_PACKAGES_OPERA);
    const packageSelections =
      savedPackagesOpera?.roomsSelections
        ?.flatMap((room: any) => room.packagesSelection ?? [])
        ?.map((pkg: any) => pkg.id) ?? [];
    const fieldsToAdd = [
      { key: 'language', value: args.language, required: false },
      { key: 'country', value: args.country, required: false },
      { key: 'adultsNumber', value: args.adultsNumber, required: false },
      { key: 'startDate', value: args.startDate, required: false },
      { key: 'endDate', value: args.endDate, required: false },
      { key: 'childrenNumber', value: args.childrenNumber, required: false },
      { key: 'nightsNumber', value: args.nightsNumber, required: false },
      { key: 'ratePlanCode', value: savedPackagesOpera.ratePlanCode, required: false },
      { key: 'channel', value: args.channel, required: false },
      { key: 'isManageBookingPage', value: args.isManageBookingPage, required: false },
      { key: 'mealInclusiveRate', value: args.showMealInclusiveRate, required: false },
      { key: 'packageSelections', value: packageSelections.join(','), required: false },
      { key: 'basketReference', value: args.basketReferenceId, required: false },
      { key: 'isCiol', value: args.isCiol, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(serviceEndpoint, getPackagesOpera, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};
