/**
 * {
    "data": {
        "companyDetails": {
            "requestedCompany": {
                "companyDetails": {
                    "companyName": "ZqiQW",
                    "alternateCompanyName": "ZqiQW",
                    "numberOfEmployees": 1,
                    "companySector": "Public administration and defence; compulsory social security",
                    "averageMonthlyBooking": "",
                    "numberOfEmployee": "",
                    "companyAddress": {
                        "addressLine1": "TestAdressLine1Z",
                        "addressLine2": "TestAdressLine2Z",
                        "addressLine3": "TestAdressLine3Z",
                        "addressLine4": "LONDONZ",
                        "addressLine5": "LONDONZ",
                        "postCode": "BL0 0BE",
                        "country": "GB"
                    },
                    "mainEmployee": {
                        "id": "EMPL_2f24bf9b-abd3-4d0b-be52-a1230ee87fa2",
                        "ghNumber": "5f2a23c0-f88a-4bd8-8f2f-f844494f5d0c",
                        "emailAddress": "automeditcompany@yopmail.com",
                        "position": "CEO",
                        "phoneNumber": "12345678",
                        "mobileNumber": "",
                        "textConfirmation": false,
                        "title": "Lady",
                        "firstName": "Jane",
                        "lastName": "Watson",
                        "centralCardId": null,
                        "address": {
                            "addressLine1": "120 Holborn",
                            "addressLine2": null,
                            "addressLine3": null,
                            "addressLine4": "LONDON",
                            "addressLine5": null,
                            "postCode": "EC1N 2TD",
                            "country": "GB"
                        },
                        "accessLevel": "SUPER",
                        "employeeStatus": "ACTIVE",
                        "dialingCode": null,
                        "employeeAnswers": {
                            "customerReferenceAnswer": null,
                            "purchaseOrderAnswer": null,
                            "userDefinedAnswers": []
                        }
                    }
                }
            }
        }
    }
}
 */
export class CompanyDetails {
  [key: string]: unknown;
  alternateCompanyName?: string;
  averageMonthlyBooking?: string;
  companyAddress?: Record<string, unknown>;
  companyName?: string;
  companySector?: string;
  mainEmployee?: Record<string, unknown>;
  numberOfEmployee?: string;
  numberOfEmployees?: number;

  /**
   * Company Address Info
   * @param data data
   * @param data.companyDetails company details address and name
   */
  constructor(data: { companyDetails?: Record<string, unknown> } = {}) {
    const companyDetails = data.companyDetails ?? {};
    this.companyName = companyDetails.companyName as string | undefined;
    this.alternateCompanyName = companyDetails.alternateCompanyName as string | undefined;
    this.numberOfEmployees = companyDetails.numberOfEmployees as number | undefined;
    this.companySector = companyDetails.companySector as string | undefined;
    this.averageMonthlyBooking = companyDetails.averageMonthlyBooking as string | undefined;
    this.numberOfEmployee = companyDetails.numberOfEmployee as string | undefined;
    this.companyAddress = companyDetails.companyAddress as Record<string, unknown> | undefined;
    this.mainEmployee = companyDetails.mainEmployee as Record<string, unknown> | undefined;
  }

  static fromResponse(data: { companyDetails?: Record<string, unknown> }): CompanyDetails {
    return new CompanyDetails(data);
  }

  /**
   * Set main contact position
   * @param position main contact position
   */
  setMainContactPosition(position: string): void {
    if (this.mainEmployee) {
      this.mainEmployee.position = position;
    }
    delete this.numberOfEmployees;
    if (this.mainEmployee) {
      delete this.mainEmployee.ghNumber;
      delete this.mainEmployee.textConfirmation;
      delete this.mainEmployee.centralCardId;
      delete this.mainEmployee.address;
      delete this.mainEmployee.accessLevel;
      delete this.mainEmployee.employeeStatus;
      delete this.mainEmployee.dialingCode;
      delete this.mainEmployee.employeeAnswers;
      delete this.mainEmployee.guestHistoryNumber;
      delete this.mainEmployee.lockedForEditing;
      delete this.mainEmployee.password;
    }
  }

  /**
   * Set additional details
   * @param companySector company sector
   * @param averageMonthlyBooking average monthly bookings
   * @param numberOfEmployee number of employees
   */
  setAdditionalDetails(companySector: string, averageMonthlyBooking: string, numberOfEmployee: string): void {
    this.companySector = companySector;
    this.averageMonthlyBooking = averageMonthlyBooking;
    this.numberOfEmployee = numberOfEmployee;
    delete this.numberOfEmployees;
    if (this.mainEmployee) {
      delete this.mainEmployee.ghNumber;
      delete this.mainEmployee.textConfirmation;
      delete this.mainEmployee.centralCardId;
      delete this.mainEmployee.address;
      delete this.mainEmployee.accessLevel;
      delete this.mainEmployee.employeeStatus;
      delete this.mainEmployee.dialingCode;
      delete this.mainEmployee.employeeAnswers;
      delete this.mainEmployee.guestHistoryNumber;
      delete this.mainEmployee.lockedForEditing;
      delete this.mainEmployee.password;
    }
  }

}
