/**
 * Employees from getEmployees
{
    "data": {
            "employees": [
                {
                    "id": "EMPL_2fe46cbe-e7f9-4b4b-9114-14a1b8ee609e",
                    "title": "Mr",
                    "firstName": "Cristina",
                    "lastName": "TM",
                    "emailAddress": "innbusiness_travelmanager@mailinator.com",
                    "accessLevel": "SUPER",
                    "employeeStatus": "ACTIVE"
                }
            ]
        }
    }
 
 */
export class Employees {
  [key: string]: unknown;
  accessLevel?: string;
  emailAddress?: string;
  employeeStatus?: string;
  firstName?: string;
  id?: string;
  lastName?: string;
  title?: string;

  /**
   * Employee constructor
   * @param data data object
   * @param data.employee employee object
   */
  constructor(data: { employee?: Record<string, unknown> } = {}) {
    const employee = data.employee ?? {};
    this.id = employee.id as string | undefined;
    this.title = employee.title as string | undefined;
    this.firstName = employee.firstName as string | undefined;
    this.lastName = employee.lastName as string | undefined;
    this.emailAddress = employee.emailAddress as string | undefined;
    this.accessLevel = employee.accessLevel as string | undefined;
    this.employeeStatus = employee.employeeStatus as string | undefined;
  }

  static fromResponse(data: { employee?: Record<string, unknown> }): Employees {
    return new Employees(data);
  }
}
