import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { handleError } from '../../../exception/error-handler';
import { HotelInformationPipelineAction } from '../actions/HotelInformationPipelineAction';
import { BookingRateInformationPipelineAction } from '../actions/BookingRateInformationPipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInformationPipelineAction } from '../actions/BookingInformationPipelineAction';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import { logPipelineError } from '../../../utils/base-utils';

const log = createLogger(basename(__filename));

export const privacyPolicy = async (
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

    return bookingInformation!.privacyPolicy;
  } catch (error: Error | any) {
    logPipelineError(error, bookingFlowCriteria, privacyPolicy);
    throw error;
  }
};
