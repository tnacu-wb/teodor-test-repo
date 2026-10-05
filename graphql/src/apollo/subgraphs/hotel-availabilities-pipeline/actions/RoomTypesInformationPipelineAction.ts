import { PipelineAction } from '../../../pipeline/PipelineAction';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from './ActionContextKeys';
import { getRoomTypeInformation } from '../../content-entity-service/services/room-type-information-service';

export class RoomTypesInformationPipelineAction implements PipelineAction {
  async execute(args: any, context: any, pipelineContext: PipelineContext): Promise<void> {
    let roomTypeInfo = await getRoomTypeInformation(args, context);
    if (!roomTypeInfo) throw new Error('Room Types Information response is empty');
    pipelineContext.set(ActionContextKeys.ROOM_TYPE_INFORMATION, roomTypeInfo);
  }
}
