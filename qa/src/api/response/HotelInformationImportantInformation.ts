/**
 * {
    "data": {
        "hotelInformation": {
            "importantInfo": {
                "title": "Important Information",
                "infoItems": [
                    {
                        "text": "The phone signal at this hotel is intermittent.",
                        "priority": "3",
                        "startDate": "01/09/2022",
                        "endDate": "29/10/2022"
                    },
                    {
                        "text": "There is no lift at this hotel.",
                        "priority": "2",
                        "startDate": "05/09/2022",
                        "endDate": "30/10/2022"
                    },
                    {
                        "text": "There is no air conditioning at this hotel.",
                        "priority": "1",
                        "startDate": "17/09/2022",
                        "endDate": "20/10/2022"
                    },
                    {
                        "text": "All twin rooms at this hotel consist of a double bed and a single sofa bed.",
                        "priority": "2",
                        "startDate": "01/08/2022",
                        "endDate": "20/08/2022"
                    }
                ]
            }
        }
    }
}
 */
export class HotelImportantInformation {
  [key: string]: unknown;
  infoItems?: Array<{ text?: string; priority?: string; startDate?: string; endDate?: string }>;
  title?: string;

  /**
   * HotelImportantInformation constructor
   * @param data object data
   * @param data.hotelInformation hotelInformation
   */
  constructor(data: { hotelInformation?: { importantInfo?: Record<string, unknown> } } = {}) {
    const importantInfo = data.hotelInformation?.importantInfo ?? {};
    this.title = importantInfo.title as string | undefined;
    this.infoItems = importantInfo.infoItems as HotelImportantInformation['infoItems'];
  }

  static fromResponse(data: { hotelInformation?: { importantInfo?: Record<string, unknown> } }): HotelImportantInformation {
    return new HotelImportantInformation(data);
  }
}
