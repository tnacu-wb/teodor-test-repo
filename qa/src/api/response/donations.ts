/**
 * Donations from API response
 * {
    "data": {
        "donations": {
            "description": "<p>We’ve all seen the humanitarian crisis affecting the people of the Ukraine. Our hearts are with all those whose lives have been affected by these devastating events, and the unimaginable suffering caused by this needless human tragedy. 100% of your donation will go to The Disasters Emergency Committee (DEC) in support of their international humanitarian aid effort. It's the task of this charity to supply food, water, first aid, medicine, warm clothes, and shelter for refugees, which is why any support you can offer today will be invaluable in helping the people of Ukraine.*</p>\n<p>&nbsp;</p>\n",
            "informationBox": null,
            "donationPackages": [
                {
                    "code": "CHRTY3",
                    "currency": "GBP",
                    "unitPrice": 5.0
                },
                {
                    "code": "CHRTY4",
                    "currency": "GBP",
                    "unitPrice": 3.0
                },
                {
                    "code": "CHRTY5",
                    "currency": "GBP",
                    "unitPrice": 1.0
                }
            ],
            "imageSrc": "/content/dam/pi/websites/desktop/booking/ukraine/dec-appeal-500x320.png",
            "name": "Support the Disasters Emergency Committee and the humanitarian effort in Ukraine"
        }
    }
}
 */
export class Donations {
  [key: string]: unknown;
  description?: string;
  donationPackages?: Array<{ code?: string; currency?: string; unitPrice?: number }>;
  imageSrc?: string;
  informationBox?: string | null;
  name?: string;

  /**
   * Donations constructor
   * @param data object data
   * @param data.donations donations
   */
  constructor(data: { donations?: Record<string, unknown> } = {}) {
    const donations = data.donations ?? {};
    this.donationPackages = donations.donationPackages as Donations['donationPackages'];
    this.name = donations.name as string | undefined;
    this.imageSrc = donations.imageSrc as string | undefined;
    this.informationBox = donations.informationBox as string | null | undefined;
    this.description = donations.description as string | undefined;
  }

  static fromResponse(data: { donations?: Record<string, unknown> }): Donations {
    return new Donations(data);
  }
}
