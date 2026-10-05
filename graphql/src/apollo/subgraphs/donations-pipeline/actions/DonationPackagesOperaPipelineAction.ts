import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { fetchDonationPackagesOpera } from '../../hotel-entity-service/services/donation-packages-service';

export class DonationPackagesOperaPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let donationPackages = await fetchDonationPackagesOpera(args, context, pipelineContext);
    pipelineContext.set(ActionContextKeys.DONATION_PACKAGES_OPERA, donationPackages);
  }
}
