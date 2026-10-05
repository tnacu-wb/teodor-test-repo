import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { HotelInformationPipelineAction } from '../actions/HotelInformationPipelineAction';
import { BookingRateInformationPipelineAction } from '../actions/BookingRateInformationPipelineAction';
import { BookingFlowIdPipelineAction } from '../actions/BookingFlowIdPipelineAction';
import { BookingInformationPipelineAction } from '../actions/BookingInformationPipelineAction';
import { DonationPackagesOperaPipelineAction } from '../actions/DonationPackagesOperaPipelineAction';
import { handleError } from '../../../exception/error-handler';
import { ActionContextKeys } from '../actions/ActionContextKeys';
import { logPipelineError } from '../../../utils/base-utils';

export const getDonations = async (
  { bookingFlowCriteria }: { bookingFlowCriteria: any },
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();

  const actions = [
    new HotelInformationPipelineAction(),
    new BookingRateInformationPipelineAction(),
    new BookingFlowIdPipelineAction(),
    new BookingInformationPipelineAction(),
    new DonationPackagesOperaPipelineAction()
  ];

  try {
    let pipelineContext = await pipelineManager.manage(actions, bookingFlowCriteria, context);
    let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION);
    let donationPackagesOpera = pipelineContext.get(ActionContextKeys.DONATION_PACKAGES_OPERA);

    let donation = bookingInfo.donation;
    if (donation?.name) {
      return {
        name: donation.name,
        description: donation.description,
        imageSrc: donation.imageSrc,
        informationBox: donation.informationBox,
        donationPackages: donationPackagesOpera?.donationPackages
      };
    } else {
      return null;
    }
  } catch (error: Error | any) {
    logPipelineError(error, bookingFlowCriteria, getDonations);
    throw error;
  }
};
