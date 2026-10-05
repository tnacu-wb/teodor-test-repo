import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';

export class BookingFlowIdPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let hotelInformation = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING);
    let rateInformation = pipelineContext.get(ActionContextKeys.RATE_INFORMATION);
    let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
    let bookingFlowId = this.getBookingFlowId(args, rateInformation, hotelInformation, bookingInfo);
    pipelineContext.set(ActionContextKeys.BOOKING_FLOW_ID, bookingFlowId);
  }

  getBookingFlowId(
    args: any,
    rateInformationResponse: any,
    hotelInformationResponse: any,
    bookingInformationResponse: any
  ): string {
    let rateCategory = '';
    for (const rate of rateInformationResponse.rateClassifications) {
      if (
        rate.ratePlanCode ===
        bookingInformationResponse.reservationByIdList[0].roomStay.ratePlanCode
      ) {
        rateCategory = rate.rateCategory;
        break;
      }
    }

    let bookingFlowId = '';
    let bookingChannel = !objectIsNullEmptyOrUndefined(args.bookingChannelCriteria?.channel)
      ? args.bookingChannelCriteria.channel
      : args.bookingChannel;
    for (const bookingFlowItem of hotelInformationResponse.bookingFlow.bookingFlowItems) {
      if (rateCategory && rateCategory === bookingFlowItem.rateCategory) {
        bookingFlowId =
          bookingChannel === 'BB' ? bookingFlowItem.bookingIdBB : bookingFlowItem.bookingId;
        break;
      }
    }

    return bookingFlowId;
  }
}
