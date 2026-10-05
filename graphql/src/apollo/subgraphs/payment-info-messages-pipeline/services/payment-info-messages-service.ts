import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { HotelInformationPipelineAction } from '../actions/HotelInformationPipelineAction';
import { BookingRateInformationPipelineAction } from '../actions/BookingRateInformationPipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInformationPipelineAction } from '../actions/BookingInformationPipelineAction';
import { handleError } from '../../../exception/error-handler';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { logPipelineError } from '../../../utils/base-utils';

/**
 * This method is used to fetch the payment info messages
 * @param bookingFlowCriteria The booking flow criteria object
 * @param context contains the header and the client
 * @returns The response containing the payment info messages
 */
export const getPaymentInfoMessagesService = async (
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

    const paymentInfoMessages: { messages: any; paymentType: any }[] = [];
    for (const item of bookingInformation.paymentInfoMessages) {
      paymentInfoMessages.push({ messages: item.messages, paymentType: item.paymentType });
    }
    return paymentInfoMessages;
  } catch (error: Error | any) {
    logPipelineError(error, bookingFlowCriteria, getPaymentInfoMessagesService);
    throw error;
  }
};
