import { PipelineContext } from '../context/PipelineContext';
import { PipelineAction } from '../PipelineAction';
import createLogger from '../../log/logger';
import { handleError } from '../../exception/error-handler';
import { basename } from 'path';

const log = createLogger(basename(__filename));

export class PipelineManager {
  async manage(actions: PipelineAction[], params: any, context: any): Promise<PipelineContext> {
    const pipelineContext = new PipelineContext();
    try {
      for (const action of actions) {
        await action.execute(params, context, pipelineContext);
      }
    } catch (error) {
      log.error(`PipelineManager stopped due to this error: ${error}`);
      throw error;
    }
    return pipelineContext;
  }
}
