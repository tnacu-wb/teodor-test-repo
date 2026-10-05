/**
 * Companies from searchCompanies
 * [
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
            ]
 */
import { Company } from './company';

export class Companies {
  [key: string]: unknown;
  companies: Company[] = [];

  /**
   * Companies constructor
   * @param data data object
   * @param data.companies companies object
   */
  constructor(data: { companies?: Array<Record<string, unknown>> } = {}) {
    const companies = data.companies ?? [];
    this.companies = companies.map((company) => new Company({ company }));
  }

  static fromResponse(data: { companies?: Array<Record<string, unknown>> }): Companies {
    return new Companies(data);
  }

  /**
   * Get company by active and restricted status
   * @param active true if it's active, else false
   * @param restricted true if it's restricted, else false
   * @returns company that matches the active and restricted status
   */
  async getCompanyByActiveAndRestrictedStatus(active = true, restricted = false): Promise<Company | undefined> {
    return this.companies.find(
      (item) => item.active === active && item.restricted === restricted && item.name !== 'A2C Test' && item.name !== 'AMEROPA-REISEN GmbH',
    );
  }

  /**
   * Get company by name and negotiatedRateEnabled status
   * @param name company name
   * @param negotiatedRateEnabled true if it's restricted, else false
   * @returns first company that matches the name and negotiatedRateEnabled status
   */
  async getCompanyByNameAndNegotiatedRateEnabledStatus(name = '', negotiatedRateEnabled = true): Promise<Company | undefined> {
    return this.companies.find((item) => item.name === name && item.negotiatedRateEnabled === negotiatedRateEnabled);
  }

}
