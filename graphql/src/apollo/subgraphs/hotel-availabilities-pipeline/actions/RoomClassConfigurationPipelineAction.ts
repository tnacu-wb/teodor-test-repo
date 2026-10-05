import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getRoomClassConfig } from '../../content-entity-service/services/room-class-config-service';

export class RoomClassConfigurationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let roomClassInfo = await getRoomClassConfig(args, context);
    if (!roomClassInfo) throw new Error('Room Class Configuration response is empty');
    pipelineContext.set(ActionContextKeys.ROOM_CLASS_CONFIG, roomClassInfo);
  }
}
