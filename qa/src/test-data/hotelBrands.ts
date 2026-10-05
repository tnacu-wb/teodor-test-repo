/** Hotel brand used by Premier Inn hotel data. */
export interface HotelBrand {
  name: string;
  nameLowercase: string;
}

/** The hotel brands used in the app. */
export class HotelBrands {
  private constructor() {}

  static readonly PI: HotelBrand = { name: 'PI', nameLowercase: 'pi' };
  static readonly PID: HotelBrand = { name: 'PID', nameLowercase: 'pid' };
  static readonly HUB: HotelBrand = { name: 'HUB', nameLowercase: 'hub' };
  static readonly ZIP: HotelBrand = { name: 'ZIP', nameLowercase: 'zip' };
  static readonly ALL: HotelBrand = { name: '', nameLowercase: '' };
}
