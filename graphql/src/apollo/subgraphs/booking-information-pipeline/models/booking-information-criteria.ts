export class BookingInformationCriteria {
  basketReference: string;
  country: string;
  language: string;
  upgradeToEmployeeRate?: boolean;
  bookingChannelCriteria: BookingChannelCriteria;

  constructor(data: any) {
    this.basketReference = data.basketReference;
    this.country = data.country;
    this.language = data.language;
    this.upgradeToEmployeeRate = data.upgradeToEmployeeRate;
    this.bookingChannelCriteria = data.bookingChannelCriteria;
  }
}

class BookingChannelCriteria {
  channel: Channel;
  subchannel: string;
  language?: string;

  constructor(data: any) {
    this.channel = data.channel;
    this.subchannel = data.subchannel;
    this.language = data.language;
  }
}

export enum Channel {
  PI = 'PI',
  BB = 'BB',
  CCUI = 'CCUI',
  DISTR = 'DISTR',
  EMPLOYEE = 'EMPLOYEE'
}
