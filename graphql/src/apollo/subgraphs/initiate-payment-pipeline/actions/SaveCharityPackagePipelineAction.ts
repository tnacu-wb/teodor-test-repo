import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { PipelineAction } from '../../../pipeline/PipelineAction';
import { ActionContextKeys } from './ActionContextKeys';
import { saveCharityPackage } from '../../hotel-reservation-entity-service/services/hotel-reservation-service';

export class SaveCharityPackagePipelineAction implements PipelineAction {
  private static readonly VALID_CHARITY_CODES = [
    'ZCHRY',
    'CHRTY',
    'ZCHR10',
    'ZCHR11',
    'ZCHR12',
    'ZCHR13'
  ];
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    const { charityPackageCode } = args.createPaymentCriteria;
    args.createPaymentCriteria.useCache = true;
    if (
      SaveCharityPackagePipelineAction.VALID_CHARITY_CODES.some((code) =>
        charityPackageCode?.includes(code)
      )
    ) {
      let charityPackageResponse = await saveCharityPackage(args, context);
      if (!charityPackageResponse) throw new Error('Charity package response is empty');
      if (charityPackageResponse.status != 200)
        throw new Error('Something went wrong while saving the Charity package');
      pipelineContext.set(ActionContextKeys.SAVE_CHARITY_PACKAGE, charityPackageResponse);
      args.createPaymentCriteria.useCache = false;
    }
  }
}
