import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getLogoAem } from '../../content-entity-service/services/get-logo-aem-service';

export class GetLogoAEMPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let getLogoAemResponse = await getLogoAem(args, context, pipelineContext);
    if (!getLogoAemResponse) throw new Error('getLogoAemResponse response is empty');
    pipelineContext.set(ActionContextKeys.GET_LOGO_AEM, getLogoAemResponse);
  }
}
