/**
 * Companies from searchCompanies
{
                    "name": "animod GmbH",
                    "arNumber": "452005~1157361",
                    "address": {
                        "addressLine1": "Bayenthalgürtel 4",
                        "addressLine2": "",
                        "addressLine3": "",
                        "addressLine4": "",
                        "country": "DE",
                        "postalCode": "50968"
                    },
                    "telephoneNumber": "",
                    "corpId": "",
                    "companyId": "2462442",
                    "profileType": "Company",
                    "language": "DE",
                    "active": true,
                    "restricted": false,
                    "negotiatedRateEnabled": false
                }
 */
export class Company {
  [key: string]: unknown;
  active?: boolean;
  address?: unknown;
  arNumber?: string;
  companyId?: string;
  corpId?: string;
  language?: string;
  name?: string;
  negotiatedRateEnabled?: boolean;
  profileType?: string;
  restricted?: boolean;
  telephoneNumber?: string;

  /**
   * Company constructor
   * @param data data object
   * @param data.company company object
   */
  constructor(data: { company?: Record<string, unknown> } = {}) {
    const company = data.company ?? {};
    this.name = company.name as string | undefined;
    this.arNumber = company.arNumber as string | undefined;
    this.address = company.address;
    this.telephoneNumber = company.telephoneNumber as string | undefined;
    this.corpId = company.corpId as string | undefined;
    this.companyId = company.companyId as string | undefined;
    this.profileType = company.profileType as string | undefined;
    this.language = company.language as string | undefined;
    this.active = company.active as boolean | undefined;
    this.restricted = company.restricted as boolean | undefined;
    this.negotiatedRateEnabled = company.negotiatedRateEnabled as boolean | undefined;
  }

  static fromResponse(data: { company?: Record<string, unknown> }): Company {
    return new Company(data);
  }
}
