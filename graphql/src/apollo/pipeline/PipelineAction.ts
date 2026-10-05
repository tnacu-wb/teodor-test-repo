import { PipelineContext } from './context/PipelineContext';

export interface PipelineAction {
  execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void>;
}
