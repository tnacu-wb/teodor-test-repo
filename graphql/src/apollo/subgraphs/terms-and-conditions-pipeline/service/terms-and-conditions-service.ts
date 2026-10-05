import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { handleError } from '../../../exception/error-handler';
import { HotelInformationPipelineAction } from '../actions/HotelInformationPipelineAction';
import { BookingRateInformationPipelineAction } from '../actions/BookingRateInformationPipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInformationPipelineAction } from '../actions/BookingInformationPipelineAction';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { logPipelineError } from '../../../utils/base-utils';

export const termsAndConditions = async (
  { bookingFlowCriteria }: { bookingFlowCriteria: any },
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  // Define the pipeline actions in order
  const actions = [
    new HotelInformationPipelineAction(),
    new BookingRateInformationPipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInformationPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(actions, bookingFlowCriteria, context);
    let bookingInformation = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION);

    return bookingInformation && bookingInformation.termsAndConditions?.length > 0
      ? { text: bookingInformation.termsAndConditions[0].text }
      : null;
  } catch (error: Error | any) {
    logPipelineError(error, bookingFlowCriteria, termsAndConditions);
    throw error;
  }
};
