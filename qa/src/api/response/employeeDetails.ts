/**
 * 
 {
    "data": {
        "getEmployeeDetails": {
            "id": "EMPL_2fe46cbe-e7f9-4b4b-9114-14a1b8ee609e",
            "ghNumber": "G81023449",
            "emailAddress": "innbusiness_travelmanager@mailinator.com",
            "position": "",
            "phoneNumber": "+44234234444",
            "mobileNumber": "",
            "textConfirmation": false,
            "title": "Mrs",
            "firstName": "Cristina",
            "lastName": "Travel Manager",
            "centralCardId": "COPC_e69303d5-ba87-4296-b938-65e6f8c88478",
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
            "dialingCode": null,
            "employeeAnswers": {
                "customerReferenceAnswer": "",
                "purchaseOrderAnswer": "22",
                "userDefinedAnswers": [
                    {
                        "miID": "COQU_371df54b-1005-4d0e-9777-9cf86667d168",
                        "miAnswer": "2"
                    },
                    {
                        "miID": "COQU_799f9bfa-bc55-46ab-b86e-4f9fb1d7c378",
                        "miAnswer": "123"
                    },
                    {
                        "miID": "COQU_6636e0ab-f972-4fb4-8254-b0413915c689",
                        "miAnswer": "1"
                    },
                    {
                        "miID": "COQU_45691095-e658-4d9a-9e85-874c9976512c",
                        "miAnswer": ""
                    }
                ]
            },
            "guestHistoryNumber": "G81023449",
            "lockedForEditing": false,
            "password": null
        }
    }
}
 */
export class EmployeeDetails {
  [key: string]: unknown;
  accessLevel?: string;
  address?: Record<string, unknown>;
  emailAddress?: string;
  employeeStatus?: string;
  firstName?: string;
  lastName?: string;
  mobileNumber?: string;
  phoneNumber?: string;
  title?: string;

  /**
   * employee details Info
   * @param data data
   * @param data.employeeDetails employee details
   */
  constructor(data: { employeeDetails?: Record<string, unknown> } = {}) {
    const employeeDetails = data.employeeDetails ?? {};
    this.address = employeeDetails.address as Record<string, unknown> | undefined;
    this.title = employeeDetails.title as string | undefined;
    this.firstName = employeeDetails.firstName as string | undefined;
    this.lastName = employeeDetails.lastName as string | undefined;
    this.emailAddress = employeeDetails.emailAddress as string | undefined;
    this.phoneNumber = employeeDetails.phoneNumber as string | undefined;
    this.mobileNumber = employeeDetails.mobileNumber as string | undefined;
    this.employeeStatus = employeeDetails.employeeStatus as string | undefined;
    this.accessLevel = employeeDetails.accessLevel as string | undefined;
  }

  static fromResponse(data: { employeeDetails?: Record<string, unknown> }): EmployeeDetails {
    return new EmployeeDetails(data);
  }
}
