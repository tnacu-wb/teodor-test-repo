import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { handleError } from '../../../exception/error-handler';
import { HotelInformationPipelineAction } from '../actions/HotelInformationPipelineAction';
import { BookingRateInformationPipelineAction } from '../actions/BookingRateInformationPipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInformationPipelineAction } from '../actions/BookingInformationPipelineAction';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { logPipelineError, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';

export const getPromotionPanel = async (
  { bookingFlowCriteria }: { bookingFlowCriteria: any },
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  const actions = [
    new HotelInformationPipelineAction(),
    new BookingRateInformationPipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInformationPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(actions, bookingFlowCriteria, context);
    let bookingInformation = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION);

    let promotionPanels: any[] = [];
    bookingInformation.promotionPanels.forEach((item: any) => {
      if (
        objectIsNullEmptyOrUndefined(item.displayHotels) ||
        item.displayHotels.includes(bookingFlowCriteria.hotelId)
      ) {
        let panel = {
          image: item.image,
          name: item.name,
          description: item.description,
          linkLabel: item.linkLabel,
          linkPath: item.linkPath
        };
        promotionPanels.push(panel);
      }
    });

    return promotionPanels;
  } catch (error: Error | any) {
    logPipelineError(error, bookingFlowCriteria, getPromotionPanel);
    throw error;
  }
};
