/**
 *
 */
export interface UserDefinedAnswerInputData {
  miAnswer?: string;
  miID?: string;
}

export class UserDefinedAnswerInput {
  [key: string]: unknown;
  miAnswer?: string;
  miID?: string;

  constructor(data: UserDefinedAnswerInputData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: UserDefinedAnswerInputData): UserDefinedAnswerInput {
    return new UserDefinedAnswerInput(data);
  }
}
