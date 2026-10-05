/**
 * Response example
 * {
   "addresses":[
      {
         "id":"GBR|1902c5d1-e0fa-47b7-80d4-2c8ae8debe03|7.730VOGBRDwfmBwAAAAABAwEAAAABlkklkgAgAAAAAAAAMQAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABTVzFXIDBOWQAAAAAA$8",
         "address":"Flat 1, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY"
      },
      {
         "id":"GBR|1902c5d1-e0fa-47b7-80d4-2c8ae8debe03|7.730wOGBRDwfmBwAAAAABAwEAAAABlkklkgAgAAAAAAAAMgAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABTVzFXIDBOWQAAAAAA$8",
         "address":"Flat 2, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY"
      },
      {
         "id":"GBR|1902c5d1-e0fa-47b7-80d4-2c8ae8debe03|7.730zOGBRDwfmBwAAAAABAwEAAAABlkklkgAgAAAAAAAAMwAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABTVzFXIDBOWQAAAAAA$8",
         "address":"Flat 3, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY"
      },
      {
         "id":"GBR|1902c5d1-e0fa-47b7-80d4-2c8ae8debe03|7.730vOGBRDwfmBwAAAAABAwEAAAABlkklkgAgAAAAAAAANAAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABTVzFXIDBOWQAAAAAA$8",
         "address":"Flat 4, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY"
      },
      {
         "id":"GBR|1902c5d1-e0fa-47b7-80d4-2c8ae8debe03|7.730sOGBRDwfmBwAAAAABAwEAAAABlkklkgAgAAAAAAAANQAA..9kAAAAAP....8AAAAAAAAAAAAAAAAAAABTVzFXIDBOWQAAAAAA$8",
         "address":"Flat 5, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY"
      }
   ]
}
 
 * Address object from api response
 */
export class AddressByPostcode {
  [key: string]: unknown;
  address?: string;
  addressText?: string;
  id?: string;

  /**
   * AddressByPostcode constructor
   * @param data object data
   * @param data.addressApiResponse response from API
   */
  constructor(data: { addressApiResponse?: Record<string, unknown> } = {}) {
    const addressApiResponse = data.addressApiResponse ?? {};
    this.id = addressApiResponse.id as string | undefined;
    this.address = addressApiResponse.address as string | undefined;
    this.addressText = addressApiResponse.addressText as string | undefined;
  }

  static fromResponse(data: { addressApiResponse?: Record<string, unknown> }): AddressByPostcode {
    return new AddressByPostcode(data);
  }
}
