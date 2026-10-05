import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getSavedPackagesOpera } from '../../hotel-reservation-entity-service/services/get-saved-packages-opera-service';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';

export class GetSavedPackagesOperaPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let getSavedPackagesOperaResponse;
    if (objectIsNullEmptyOrUndefined(args.basketReferenceId) || args.basketReferenceId === 'null') {
      getSavedPackagesOperaResponse = 'SKIPPED';
    } else {
      getSavedPackagesOperaResponse = await getSavedPackagesOpera(args, context, pipelineContext);
    }
    if (!getSavedPackagesOperaResponse)
      throw new Error('getSavedPackagesOperaResponse response is empty');
    pipelineContext.set(ActionContextKeys.GET_SAVED_PACKAGES_OPERA, getSavedPackagesOperaResponse);
  }
}
