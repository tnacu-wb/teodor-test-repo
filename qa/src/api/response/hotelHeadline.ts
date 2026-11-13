/**
 * Hotel Headline class
 */
export class HotelHeadline {
  [key: string]: unknown;
  headline?: string;

  /**
   * HotelHeadline constructor
   * @param data object data
   * @param data.headline headline
   */
  constructor(data: { headline?: string } = {}) {
    this.headline = data.headline;
  }

  static fromResponse(data: { headline?: string }): HotelHeadline {
    return new HotelHeadline(data);
  }
}
