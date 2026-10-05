import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { fetchRoomType } from '../../content-entity-service/services/room-type-information-service';

export class RoomTypePipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let roomType = await fetchRoomType(args, context, pipelineContext);
    if (!roomType) throw new Error('Room type response is empty');
    pipelineContext.set(ActionContextKeys.ROOM_TYPE, roomType);
  }
}
