import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import {
  HotelInformationResponse,
  RateInformationResponse
} from '../../content-entity-service/models/content-entity-models';

export class BookingFlowIdPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let hotelInformation = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION);
    let rateInformation = pipelineContext.get(ActionContextKeys.RATE_INFORMATION);
    let bookingFlowId = this.getBookingFlowId(args, rateInformation, hotelInformation);
    pipelineContext.set(ActionContextKeys.BOOKING_FLOW_ID, bookingFlowId);
  }

  getBookingFlowId(
    args: any,
    rateInformationResponse: RateInformationResponse,
    hotelInformationResponse: HotelInformationResponse
  ): string {
    let rateCategory = '';
    for (const rate of rateInformationResponse.rateClassifications) {
      if (rate.ratePlanCode === args.rateCode) {
        rateCategory = rate.rateCategory;
        break;
      }
    }

    let bookingFlowId = '';
    for (const bookingFlowItem of hotelInformationResponse.bookingFlow.bookingFlowItems) {
      if (rateCategory === bookingFlowItem.rateCategory) {
        bookingFlowId =
          args.bookingChannel === 'BB' ? bookingFlowItem.bookingIdBB : bookingFlowItem.bookingId;
        break;
      }
    }

    return bookingFlowId;
  }
}
