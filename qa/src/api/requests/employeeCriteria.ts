import type { EmployeeAnswersInput } from './employeeAnswersInput';
import type { AddressInfoInput } from './addressInfoInput';

/**
 * input EmployeeCriteria {
id: String
ghNumber: String
emailAddress: String!
position: String
phoneNumber: String
mobileNumber: String
textConfirmation: Boolean
title: String
firstName: String!
lastName: String!
centralCardId: String
address: AddressInfoInput
accessLevel: AccessLevel
employeeStatus: EmployeeStatus
dialingCode: String
employeeAnswers: EmployeeAnswersInput
guestHistoryNumber: String
lockedForEditing: Boolean
password: String
}
 */
export interface EmployeeCriteriaData {
  accessLevel?: string;
  address?: AddressInfoInput | string;
  centralCardId?: string;
  dialingCode?: string;
  emailAddress?: string;
  employeeAnswers?: EmployeeAnswersInput;
  employeeStatus?: string;
  firstName?: string;
  ghNumber?: string;
  guestHistoryNumber?: string;
  id?: string;
  lastName?: string;
  lockedForEditing?: boolean;
  mobileNumber?: string;
  password?: string;
  phoneNumber?: string;
  position?: string;
  textConfirmation?: boolean;
  title?: string;
}

export class EmployeeCriteria {
  [key: string]: unknown;
  accessLevel?: string;
  address?: AddressInfoInput | string;
  centralCardId?: string;
  dialingCode?: string;
  emailAddress?: string;
  employeeAnswers?: EmployeeAnswersInput;
  employeeStatus?: string;
  firstName?: string;
  ghNumber?: string;
  guestHistoryNumber?: string;
  id?: string;
  lastName?: string;
  lockedForEditing?: boolean;
  mobileNumber?: string;
  password?: string;
  phoneNumber?: string;
  position?: string;
  textConfirmation?: boolean;
  title?: string;

  constructor(data: EmployeeCriteriaData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: EmployeeCriteriaData): EmployeeCriteria {
    return new EmployeeCriteria(data);
  }
}
