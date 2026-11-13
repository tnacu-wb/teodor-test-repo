/**
 * Booking channel object required in hotelAvailability request
 */
import { Locales } from '../../test-data/locales';

export interface BookingChannelData {
  channel?: string;
  subchannel?: string;
  language?: string;
}

export class BookingChannel {
  [key: string]: unknown;
  channel?: string;
  language?: string;
  subchannel?: string;

  constructor({
    channel,
    subchannel,
    language,
  }: BookingChannelData = {}) {
    const runtimeOptions = (global.browser?.options as Record<string, unknown> | undefined) ?? {};
    const app = String(runtimeOptions.app ?? 'pi');
    const locale = Locales.getLocaleByString(String(runtimeOptions.locale ?? 'gb-en'));

    this.channel = channel ?? (app === 'pib' ? 'BB' : app.toUpperCase());
    this.subchannel = subchannel ?? 'WEB';
    this.language = language ?? locale.language.toUpperCase();
  }

  static fromRequest(data: BookingChannelData): BookingChannel {
    return new BookingChannel(data);
  }
}
