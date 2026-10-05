import { Channel } from './channel';

export class BookingChannelCriteria {
  channel: Channel;
  subchannel: string;
  language?: string;

  constructor(data: BoockingCriteria) {
    this.channel = data.channel;
    this.subchannel = data.subchannel;
    this.language = data.language;
  }
}

export interface BoockingCriteria {
  channel: Channel;
  subchannel: string;
  language?: string;
}
