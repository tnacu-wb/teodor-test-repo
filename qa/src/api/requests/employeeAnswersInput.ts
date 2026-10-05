/**
 * input EmployeeAnswersInput {
customerReferenceAnswer: String
purchaseOrderAnswer: String
userDefinedAnswers: [UserDefinedAnswerInput]
}
 */
import type { UserDefinedAnswerInput as UserDefinedAnswerInputType } from './userDefinedAnswerInput';

export interface EmployeeAnswersInputData {
  customerReferenceAnswer?: string;
  purchaseOrderAnswer?: string;
  userDefinedAnswers?: UserDefinedAnswerInputType[];
}

export class EmployeeAnswersInput {
  [key: string]: unknown;
  customerReferenceAnswer?: string;
  purchaseOrderAnswer?: string;
  userDefinedAnswers?: UserDefinedAnswerInputType[];

  constructor(data: EmployeeAnswersInputData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: EmployeeAnswersInputData): EmployeeAnswersInput {
    return new EmployeeAnswersInput(data);
  }
}
