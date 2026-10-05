export class BookingChannelCriteria {
  channel: string;
  subchannel: string;
  language?: string;

  constructor(data: BookingChannelCriteria) {
    this.channel = data.channel;
    this.subchannel = data.subchannel;
    this.language = data.language;
  }
}
