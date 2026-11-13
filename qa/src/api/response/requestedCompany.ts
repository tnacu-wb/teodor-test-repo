/**
 * Requested company
{
    "requestedCompany": {
        "companyDetails": {
            "companyName": "Inn Business Company Testing_M",
            "alternateCompanyName": "Inn Business Company Testing_M",
            "numberOfEmployees": 4,
            "companyAddress": {
                "addressLine1": "25 Truthan View",
                "addressLine2": "Trispen",
                "addressLine3": "",
                "addressLine4": "TRURO",
                "addressLine5": "",
                "postCode": "TR4 9AS",
                "country": "GB"
            },
            "mainEmployee": {
                "id": "EMPL_2fe46cbe-e7f9-4b4b-9114-14a1b8ee609e",
                "ghNumber": "G81023449",
                "emailAddress": "innbusiness_travelmanager@mailinator.com",
                "position": "",
                "phoneNumber": "234234444",
                "mobileNumber": "",
                "textConfirmation": false,
                "title": "Mrs",
                "firstName": "Cristina",
                "lastName": "Travel Manager",
                "centralCardId": "1",
                "address": {
                    "addressLine1": "25 Truthan View",
                    "addressLine2": "Trispen",
                    "addressLine3": "",
                    "addressLine4": "TRURO",
                    "addressLine5": "",
                    "postCode": "TR4 9AS",
                    "country": "GB"
                },
                "accessLevel": "SUPER",
                "employeeStatus": "ACTIVE",
                "employeeAnswers": {}
            }
        },
        "paymentDetails": {
            "profileLocked": false,
            "allowIndividualCards": false,
            "paymentCards": [
                {
                    "cardId": "1",
                    "cardLabel": "Visa",
                    "cardType": "VI",
                    "nameOnCard": "Inn business",
                    "cardNumber": "************1103",
                    "expiryDate": "1226",
                    "cardToken": "4216333880397891103",
                    "billingAddress": {
                        "addressLine1": "25 Truthan View",
                        "addressLine2": "Trispen",
                        "addressLine3": "",
                        "addressLine4": "TRURO",
                        "addressLine5": "",
                        "postCode": "TR4 9AS",
                        "country": "GB"
                    },
                    "cardNotPresentRequired": true,
                    "cardNotPresent": {
                        "businessAccountUsername": "",
                        "businessAccountPassword": ""
                    }
                },
                {
                    "cardId": "COPC_2413474b-2c9b-41b9-a422-d9f3c54a77b9",
                    "cardLabel": "claudia's card",
                    "cardType": "AC",
                    "nameOnCard": "claudia doe test",
                    "cardNumber": "************4444",
                    "expiryDate": "0332",
                    "cardToken": "5830601625431984444",
                    "billingAddress": {
                        "addressLine1": "25 Truthan View",
                        "addressLine2": "Trispen",
                        "addressLine3": "",
                        "addressLine4": "TRURO",
                        "addressLine5": "",
                        "postCode": "TR4 9AS",
                        "country": "GB"
                    },
                    "cardNotPresentRequired": true,
                    "cardNotPresent": {}
                },
                {
                    "cardId": "COPC_b34788b6-b7fc-4ea9-a0d9-8be1126200ab",
                    "cardLabel": "claudia's card!",
                    "cardType": "AC",
                    "nameOnCard": "claudia doe",
                    "cardNumber": "************4444",
                    "expiryDate": "0135",
                    "cardToken": "5830601625431984444",
                    "billingAddress": {
                        "addressLine1": "157 Watford Road, Croxley Green",
                        "addressLine4": "RICKMANSWORTH",
                        "postCode": "WD3 3ED",
                        "country": "GB"
                    },
                    "cardNotPresentRequired": true,
                    "cardNotPresent": {
                        "businessAccountUsername": "",
                        "businessAccountPassword": "123Card"
                    }
                }
            ]
        },
        "bookingAllowances": {
            "maxDinnerBudgets": {
                "uKWide": {
                    "amount": 0,
                    "currency": "GBP"
                },
                "greaterLondon": {
                    "amount": 0,
                    "currency": "GBP"
                },
                "ireland": {
                    "amount": 0,
                    "currency": "EUR"
                }
            },
            "extrasCodes": [
                "1",
                "2",
                "3",
                "4",
                "5"
            ],
            "upsellItemsAllowed": [
                "11",
                "15",
                "12",
                "17",
                "18",
                "135",
                "136",
                "137"
            ],
            "allowAlcohol": false,
            "allowCarParking": false,
            "allowAdditionalCosts": false,
            "allowPremierSaverRates": true,
            "allowIndividualCards": true,
            "maxNumberOfNights": 14
        },
        "bookingAlerts": {
            "rateCaps": {
                "uKWide": {
                    "amount": 0,
                    "currency": "GBP"
                },
                "greaterLondon": {
                    "amount": 0,
                    "currency": "GBP"
                },
                "ireland": {
                    "amount": 0,
                    "currency": "EUR"
                }
            },
            "bookingAlertHotels": [
                ""
            ],
            "recipientEmailAddresses": [
                ""
            ],
            "dayOfArrival": false,
            "weekendArrival": false,
            "passThroughWeekend": false
        },
        "companyManagementDetails": {
            "purchaseOrderManagement": {
                "label": "",
                "mandatory": false,
                "managementHeader": "",
                "active": false,
                "managementInformationAnswer": {},
                "positionId": 0
            },
            "customerReferenceManagement": {
                "label": "",
                "mandatory": false,
                "managementHeader": "",
                "active": false,
                "managementInformationAnswer": {},
                "positionId": 0
            },
            "userDefinedManagement": []
        },
        "companyCellCodes": [
            {
                "type": "1",
                "description": "BFLEX"
            }
        ],
        "restrictedRatePlans": [],
        "restrictedHotelCodes": [],
        "companyStatus": {
            "status": false
        }
    },
    "companyCellCodes": [
        {
            "type": "1",
            "description": "BFLEX"
        }
    ],
    "allowCentralCreditCard": true,
    "marketingAllowed": false,
    "companyLockedForEditing": false,
    "success": true
}
 */
export class RequestedCompany {
  [key: string]: unknown;
  requestedCompany?: {
    companyDetails: { companyName?: string };
    bookingAllowances: {
      ukDinnerAmount?: number;
      greaterLondonDinnerAmount?: number;
      irelandDinnerAmount?: number;
      extrasCodes?: string[];
      upsellItemsAllowed?: string[];
      allowAlcohol?: boolean;
      allowCarParking?: boolean;
      allowAdditionalCosts?: boolean;
      allowPremierSaverRates?: boolean;
      allowIndividualCards?: boolean;
      maxNumberOfNights?: number;
    };
  };

  /**
   * Requested company constructor
   * @param data data object
   * @param data.company company object
   */
  constructor(data: { company?: Record<string, unknown> } = {}) {
    const company = data.company ?? {};
    const requestedCompany = (company.requestedCompany ?? {}) as Record<string, unknown>;
    const companyDetails = (requestedCompany.companyDetails ?? {}) as Record<string, unknown>;
    const bookingAllowances = (requestedCompany.bookingAllowances ?? {}) as Record<string, unknown>;
    const maxDinnerBudgets = (bookingAllowances.maxDinnerBudgets ?? {}) as Record<string, unknown>;
    const uKWide = (maxDinnerBudgets.uKWide ?? {}) as Record<string, unknown>;
    const greaterLondon = (maxDinnerBudgets.greaterLondon ?? {}) as Record<string, unknown>;
    const ireland = (maxDinnerBudgets.ireland ?? {}) as Record<string, unknown>;

    this.requestedCompany = {
      companyDetails: {
        companyName: companyDetails.companyName as string | undefined,
      },
      bookingAllowances: {
        ukDinnerAmount: uKWide.amount as number | undefined,
        greaterLondonDinnerAmount: greaterLondon.amount as number | undefined,
        irelandDinnerAmount: ireland.amount as number | undefined,
        extrasCodes: bookingAllowances.extrasCodes as string[] | undefined,
        upsellItemsAllowed: bookingAllowances.upsellItemsAllowed as string[] | undefined,
        allowAlcohol: bookingAllowances.allowAlcohol as boolean | undefined,
        allowCarParking: bookingAllowances.allowCarParking as boolean | undefined,
        allowAdditionalCosts: bookingAllowances.allowAdditionalCosts as boolean | undefined,
        allowPremierSaverRates: bookingAllowances.allowPremierSaverRates as boolean | undefined,
        allowIndividualCards: bookingAllowances.allowIndividualCards as boolean | undefined,
        maxNumberOfNights: bookingAllowances.maxNumberOfNights as number | undefined,
      },
    };
  }

  static fromResponse(data: { company?: Record<string, unknown> }): RequestedCompany {
    return new RequestedCompany(data);
  }
}
