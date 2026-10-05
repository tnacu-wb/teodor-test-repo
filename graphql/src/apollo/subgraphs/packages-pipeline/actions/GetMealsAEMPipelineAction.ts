import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getMealsAem } from '../../content-entity-service/services/get-meals-aem-service';

export class GetMealsAEMPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let getMealsAemResponse = await getMealsAem(args, context, pipelineContext);
    if (!getMealsAemResponse) throw new Error('getMealsAemResponse response is empty');
    pipelineContext.set(ActionContextKeys.GET_MEALS_AEM, getMealsAemResponse);
  }
}
