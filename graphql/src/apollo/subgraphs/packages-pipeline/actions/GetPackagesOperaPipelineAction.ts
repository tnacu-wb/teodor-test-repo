import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { initiatePayment } from '../../basket-service/services/payment-service';
import { ActionContextKeys } from './ActionContextKeys';
import { getPackagesOpera } from '../../hotel-entity-service/services/get-packages-opera-service';

export class GetPackagesOperaPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let getPackagesOperaResponse = await getPackagesOpera(args, context, pipelineContext);
    if (!getPackagesOperaResponse) throw new Error('getPackagesOperaResponse response is empty');
    pipelineContext.set(ActionContextKeys.GET_PACKAGES_OPERA, getPackagesOperaResponse);
  }
}
