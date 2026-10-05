/**
 {
    "data": {
        "getApplicationDetails": {
            "applicationId": "785940",
            "applicationNumber": "785940",
            "applicationGuid": "cfac3af2-04f0-42f9-ace4-062785a8fbd8",
            "companyId": 1010531881,
            "scheme": "GB",
            "accountName": "IB Automation Ltd",
            "status": "Incomplete Application",
            "participants": [
                {
                    "initiator": true,
                    "participantId": 1010532430,
                    "delegated": false,
                    "terms": false,
                    "directDebit": false,
                    "email": "resume-companydetails@yopmail.com",
                    "shared": "2025-07-02T11:56:08.3604096Z",
                    "name": "Companydetails  Payapp Resume"
                }
            ],
            "cardHolders": null,
            "contactDetails": {
                "title": "Mr",
                "foreName": "Companydetails ",
                "lastName": "Payapp Resume",
                "position": "QA",
                "telephone": "02071235555",
                "mobile": "",
                "email": "resume-companydetails@yopmail.com"
            },
            "companyDetails": {
                "vatRegistrationNumber": null,
                "estMonthlySpend": null,
                "companyType": null,
                "charityNumber": null,
                "companyRegNum": null,
                "partnerDetails": null,
                "timeTradingId": null,
                "registrationAddress": null,
                "correspondenceAddress": null,
                "correspondenceContactInfo": null,
                "hotelBrandPolicy": null,
                "parentCompanyName": null,
                "industrySector": null,
                "numberOfEmployees": null,
                "companyNameOnCard": null
            },
            "cardDetails": [],
            "hostedPageGuid": null,
            "directDebitOption": null
        }
    }
}
 */
export class ApplicationDetails {
  [key: string]: unknown;
  accountName?: string;
  applicationId?: string;
  cardDetails?: unknown[];
  cardHolders?: unknown;
  companyDetails?: Record<string, unknown>;
  contactDetails?: Record<string, unknown>;
  created?: unknown;
  participants?: Array<Record<string, unknown>>;

  /**
   * Application details constructor
   * @param data object data
   * @param data.applicationDetails applicationDetails
   */
  constructor(data: { applicationDetails?: Record<string, unknown> } = {}) {
    const applicationDetails = data.applicationDetails ?? {};
    this.applicationId = applicationDetails.applicationId as string | undefined;
    this.accountName = applicationDetails.accountName as string | undefined;
    this.created = applicationDetails.created;
    this.participants = applicationDetails.participants as Array<Record<string, unknown>> | undefined;
    this.cardHolders = applicationDetails.cardHolders;
    this.contactDetails = applicationDetails.contactDetails as Record<string, unknown> | undefined;
    this.companyDetails = applicationDetails.companyDetails as Record<string, unknown> | undefined;
    this.cardDetails = applicationDetails.cardDetails as unknown[] | undefined;
  }

  static fromResponse(data: { applicationDetails?: Record<string, unknown> }): ApplicationDetails {
    return new ApplicationDetails(data);
  }
}
