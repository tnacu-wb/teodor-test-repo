/**
 * Hotel Title class
 */
export class HotelTitle {
  [key: string]: unknown;
  brand?: string;
  title?: string;

  /**
   * HotelTitle constructor
   * @param data object data
   * @param data.title title data
   * @param data.brand brand data
   */
  constructor(data: { title?: string; brand?: string } = {}) {
    this.title = data.title;
    this.brand = data.brand;
  }

  static fromResponse(data: { title?: string; brand?: string }): HotelTitle {
    return new HotelTitle(data);
  }
}
