import { MultiHotelInformationPipelineAction } from '../actions/MultiHotelInformationPipelineAction';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { handleError } from '../../../exception/error-handler';
import { MultiHotelAvailabilitiesV2PipelineAction } from '../actions/MultiHotelAvailabilitiesV2PipelineAction';
import { MultiHotelAvailabilitiesPipelineAction } from '../actions/MultiHotelAvailabilitiesPipelineAction';
import { logPipelineError } from '../../../utils/base-utils';
import { promotionsInformation } from '../../content-entity-service/services/global-config-service';

export const getHotelAvailabilitiesV2 = async (
  availabilitiesSearchCriteria: any,
  context: any
): Promise<any> => {
  const response = await getHotelAvailabilitiesCommon(availabilitiesSearchCriteria, context, [
    new MultiHotelAvailabilitiesV2PipelineAction(),
    new MultiHotelInformationPipelineAction()
  ]);
  let promotionInformationResponse = null;

  const criteria = availabilitiesSearchCriteria.availabilitiesSearchCriteria;
  const hasStartDate = criteria.startDate != null && criteria.startDate.trim() !== '';
  const hasEndDate = criteria.endDate != null && criteria.endDate.trim() !== '';

  if (
    criteria.page === 1 &&
    criteria.promotionCode !== undefined &&
    hasStartDate &&
    hasEndDate &&
    response.multiHotelAvailabilities.length > 0
  ) {
    const brand = response.multiHotelAvailabilities[0]?.hotelInformation?.brand;
    const promotionArgs = {
      promotionsInformationCriteria: {
        promotionCode: criteria.promotionCode,
        country: criteria.country,
        language: criteria.language,
        channel: criteria.channel,
        brand,
        stayStartDate: criteria.startDate,
        stayEndDate: criteria.endDate
      }
    };

    promotionInformationResponse = await promotionsInformation(promotionArgs, context);
  }

  return {
    ...response,
    promotionsInformation: promotionInformationResponse
  };
};

export const getHotelAvailabilities = async (
  availabilitiesSearchCriteria: any,
  context: any
): Promise<any> => {
  return await getHotelAvailabilitiesCommon(availabilitiesSearchCriteria, context, [
    new MultiHotelAvailabilitiesPipelineAction(),
    new MultiHotelInformationPipelineAction()
  ]);
};

const getHotelAvailabilitiesCommon = async (
  availabilitiesSearchCriteria: any,
  context: any,
  actions: any[]
): Promise<any> => {
  const pipelineManager = new PipelineManager();
  try {
    const pipelineContext = await pipelineManager.manage(
      actions,
      availabilitiesSearchCriteria,
      context
    );
    const availabilities = pipelineContext.get(ActionContextKeys.MULTI_HOTEL_AVAILABILITIES);
    const multiHotelInformation = pipelineContext.get(ActionContextKeys.MULTI_HOTEL_INFORMATION);

    const multiHotelAvailabilities: any[] = [];
    availabilities.hotelAvailabilities.forEach((availability: any) => {
      let hotelInformation = multiHotelInformation.find(
        (hotelInfo: any) => hotelInfo.hotelId === availability.hotelId
      );
      if (hotelInformation) {
        let singleHotelAvailability = {
          hotelId: availability.hotelId,
          name: hotelInformation.name,
          hotelAvailability: availability,
          hotelInformation: hotelInformation
        };
        multiHotelAvailabilities.push(singleHotelAvailability);
      }
    });
    const finalHotelAvailabilities = {
      multiHotelAvailabilities: multiHotelAvailabilities,
      page: availabilities.page,
      pageSize: availabilities.pageSize,
      total: availabilities.total
    };
    return finalHotelAvailabilities;
  } catch (error: Error | any) {
    logPipelineError(error, availabilitiesSearchCriteria, getHotelAvailabilities);
    throw error;
  }
};
