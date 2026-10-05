/**
 * response example:
 {
   "data": {
      "getCompanyRegistrationQuestionsAndAnswers": {
         "purchaseOrderManagement": {
            "questionId": "2",
            "label": "Purchase order number?",
            "mandatory": false,
            "managementHeader": "123",
            "active": true,
            "location": "R",
            "managementInformationAnswer": {
               "answerType": null,
               "answers": null
            },
            "type": null,
            "positionId": 0,
            "answer": "22"
         },
         "customerReferenceManagement": {
            "questionId": "1",
            "label": "Customer reference?",
            "mandatory": false,
            "managementHeader": "Customer reference",
            "active": true,
            "location": "R",
            "managementInformationAnswer": {
               "answerType": "F",
               "answers": null
            },
            "type": "customer reference",
            "positionId": 0,
            "answer": ""
         },
         "userDefinedQuestions": [
            {
               "questionId": "COQU_371df54b-1005-4d0e-9777-9cf86667d168",
               "label": "mandatory one?",
               "mandatory": true,
               "managementHeader": "test label",
               "active": true,
               "location": "R",
               "managementInformationAnswer": {
                  "answerType": "U",
                  "answers": [
                     "option 1",
                     "option 2"
                  ]
               },
               "type": null,
               "positionId": 1,
               "userDefinedAnswer": "2"
            },
            {
               "questionId": "COQU_799f9bfa-bc55-46ab-b86e-4f9fb1d7c378",
               "label": "mandatory two?",
               "mandatory": true,
               "managementHeader": "test label two",
               "active": true,
               "location": "R",
               "managementInformationAnswer": {
                  "answerType": "F",
                  "answers": null
               },
               "type": null,
               "positionId": 2,
               "userDefinedAnswer": "123"
            },
            {
               "questionId": "COQU_6636e0ab-f972-4fb4-8254-b0413915c689",
               "label": "mandatory three?",
               "mandatory": true,
               "managementHeader": "test label three",
               "active": true,
               "location": "R",
               "managementInformationAnswer": {
                  "answerType": "U",
                  "answers": [
                     "answer one"
                  ]
               },
               "type": null,
               "positionId": 3,
               "userDefinedAnswer": "1"
            },
            {
               "questionId": "COQU_45691095-e658-4d9a-9e85-874c9976512c",
               "label": "q?",
               "mandatory": true,
               "managementHeader": "test label four",
               "active": true,
               "location": "R",
               "managementInformationAnswer": {
                  "answerType": "F",
                  "answers": null
               },
               "type": null,
               "positionId": 4,
               "userDefinedAnswer": ""
            }
         ]
      }
   }
}
 */
import { ManagementInformationQuestionAnswered } from './managementInformationQuestionAnswered';
import { UserDefinedQuestionAnswered } from './userDefinedQuestionAnswered';

export class AnsweredQuestions {
  [key: string]: unknown;
  customerReferenceManagement?: ManagementInformationQuestionAnswered;
  purchaseOrderManagement?: ManagementInformationQuestionAnswered;
  userDefinedQuestions: UserDefinedQuestionAnswered[] = [];

  /**
   * AnsweredQuestions constructor
   * @param data object data
   * @param data.answeredQuestions answeredQuestions data
   */
  constructor(data: { answeredQuestions?: Record<string, unknown> } = {}) {
    const answeredQuestions = data.answeredQuestions ?? {};
    this.purchaseOrderManagement = new ManagementInformationQuestionAnswered({
      managementInformationQuestionAnswered: answeredQuestions.purchaseOrderManagement as Record<string, unknown>,
    });
    this.customerReferenceManagement = new ManagementInformationQuestionAnswered({
      managementInformationQuestionAnswered: answeredQuestions.customerReferenceManagement as Record<string, unknown>,
    });
    const userDefinedQuestions = Array.isArray(answeredQuestions.userDefinedQuestions)
      ? (answeredQuestions.userDefinedQuestions as Array<Record<string, unknown>>)
      : [];
    this.userDefinedQuestions = userDefinedQuestions.map(
      (userDefinedQuestion) => new UserDefinedQuestionAnswered({ userDefinedQuestionAnswered: userDefinedQuestion }),
    );
  }

  static fromResponse(data: { answeredQuestions?: Record<string, unknown> }): AnsweredQuestions {
    return new AnsweredQuestions(data);
  }

  /**
   * Get registration questions
   * @returns returns a map with registration question, mandatory/optional pairs
   */
  async getRegistrationQuestions(): Promise<Map<string, string>> {
    const registrationQuestions = new Map<string, string>();
    if (this.purchaseOrderManagement?.active) {
      const purchaseOrderManagementQuestionLabel = this.purchaseOrderManagement.mandatory ? 'mandatory' : 'optional';
      registrationQuestions.set(this.purchaseOrderManagement.label ?? '', purchaseOrderManagementQuestionLabel);
    }
    if (this.customerReferenceManagement?.active) {
      const customerReferenceManagementQuestionLabel = this.customerReferenceManagement.mandatory ? 'mandatory' : 'optional';
      registrationQuestions.set(this.customerReferenceManagement.label ?? '', customerReferenceManagementQuestionLabel);
    }
    this.userDefinedQuestions
      .filter((item) => item.active)
      .forEach((item) => {
        const userDefinedQuestionLabel = item.mandatory ? 'mandatory' : 'optional';
        registrationQuestions.set(item.label ?? '', userDefinedQuestionLabel);
      });
    return registrationQuestions;
  }

  /**
   * Get registration questions and answers
   * @returns returns a map with registration question and answer pairs
   */
  async getRegistrationQuestionsAndAnswers(): Promise<Map<string, string | null>> {
    const registrationQuestionsAndAnswers = new Map<string, string | null>();
    if (this.purchaseOrderManagement?.active) {
      registrationQuestionsAndAnswers.set(this.purchaseOrderManagement.label ?? '', this.purchaseOrderManagement.answer ?? null);
    }
    if (this.customerReferenceManagement?.active) {
      registrationQuestionsAndAnswers.set(this.customerReferenceManagement.label ?? '', this.customerReferenceManagement.answer ?? null);
    }
    this.userDefinedQuestions
      .filter((item) => item.active)
      .forEach((item) => {
        const mia = item.managementInformationAnswer;
        let value: string | null = null;
        if (mia && Array.isArray(mia.answers) && mia.answers.length > 0 && mia.answers[0] != null) {
          value = mia.answers[0];
        } else if (item.userDefinedAnswer != null) {
          value = item.userDefinedAnswer;
        }
        registrationQuestionsAndAnswers.set(item.label ?? '', value);
      });
    return registrationQuestionsAndAnswers;
  }

  /**
   * Get user defined question Id by question label
   * @param questionLabel question label
   * @returns returns question Id
   */
  async getUserDefinedQuestionIdByQuestionLabel(questionLabel?: string): Promise<string | undefined> {
    return this.userDefinedQuestions.find((item) => item.label === questionLabel)?.questionId;
  }

  /**
   * Get user defined answer type Id by question Id
   * @param questionId question Id
   * @returns returns answer type
   */
  async getUserDefinedAnswerTypeByQuestionId(questionId?: string): Promise<string | null | undefined> {
    return this.userDefinedQuestions.find((item) => item.questionId === questionId)?.managementInformationAnswer?.answerType;
  }

  /**
   * Get answer type list
   * @returns returns a list of answer types
   */
  async getAnswerTypes(): Promise<Array<string | null | undefined>> {
    const answerTypes: Array<string | null | undefined> = [];
    if (this.purchaseOrderManagement?.active) {
      answerTypes.push(this.purchaseOrderManagement.managementInformationAnswer?.answerType);
    }
    if (this.customerReferenceManagement?.active) {
      answerTypes.push(this.customerReferenceManagement.managementInformationAnswer?.answerType);
    }
    this.userDefinedQuestions
      .filter((item) => item.active)
      .forEach((item) => {
        answerTypes.push(item.managementInformationAnswer?.answerType);
      });
    return answerTypes;
  }

  /**
   * Get answer type list for mandatory questions
   * @returns returns a list of answer types for mandatory questions
   */
  async getMandatoryAnswerTypes(): Promise<Array<string | null | undefined>> {
    const answerTypes: Array<string | null | undefined> = [];
    if (this.purchaseOrderManagement?.active && this.purchaseOrderManagement.mandatory) {
      answerTypes.push(this.purchaseOrderManagement.managementInformationAnswer?.answerType);
    }
    if (this.customerReferenceManagement?.active && this.customerReferenceManagement.mandatory) {
      answerTypes.push(this.customerReferenceManagement.managementInformationAnswer?.answerType);
    }
    this.userDefinedQuestions
      .filter((item) => item.active && item.mandatory)
      .forEach((item) => {
        answerTypes.push(item.managementInformationAnswer?.answerType);
      });
    return answerTypes;
  }

  /**
   * Get answer list
   * @returns returns a list of answer options
   */
  async getAnswers(): Promise<Array<string[] | null | undefined>> {
    const answers: Array<string[] | null | undefined> = [];
    if (this.purchaseOrderManagement?.active) {
      answers.push(this.purchaseOrderManagement.managementInformationAnswer?.answers);
    }
    if (this.customerReferenceManagement?.active) {
      answers.push(this.customerReferenceManagement.managementInformationAnswer?.answers);
    }
    this.userDefinedQuestions
      .filter((item) => item.active)
      .forEach((item) => {
        answers.push(item.managementInformationAnswer?.answers);
      });
    return answers;
  }

  /**
   * Get user defined answers
   * @returns returns a list of user defined answers
   */
  async getUserDefinedAnswers(): Promise<Array<string | null | undefined>> {
    const userDefinedAnswer: Array<string | null | undefined> = [];
    if (this.purchaseOrderManagement?.active === true) {
      userDefinedAnswer.push(this.purchaseOrderManagement.answer);
    }
    if (this.customerReferenceManagement?.active === true) {
      userDefinedAnswer.push(this.customerReferenceManagement.answer);
    }
    this.userDefinedQuestions
      .filter((item) => item.active === true)
      .forEach((item) => {
        userDefinedAnswer.push(item.userDefinedAnswer);
      });
    return userDefinedAnswer;
  }

  /**
   * Extracts and returns a list of questionIds from userDefinedQuestions
   * @returns list of questionIds
   */
  getUserDefinedQuestionIdList(): Array<string | undefined> {
    return this.userDefinedQuestions.map((item) => item.questionId);
  }

  /**
   * Validates that at least 1 registration question exists
   * @param registrationQuestions registration questions map
   */
  validateRegistrationQuestionsExist(registrationQuestions?: Map<string, unknown>): void {
    console.log(`Validate registration questions exist - found ${registrationQuestions?.size ?? 0} registration questions`);
    if ((registrationQuestions?.size ?? 0) <= 0) {
      throw new Error('Expected at least 1 registration question to be present');
    }
  }

  /**
   * Validates that the number of answer types matches the number of registration questions.
   * @param registrationQuestions registration questions map
   * @param answerTypes list of answer types
   */
  validateRegistrationQuestionsAnswerTypes(registrationQuestions?: Map<string, unknown>, answerTypes?: unknown[]): void {
    const questionsCount = registrationQuestions?.size ?? 0;
    const answerTypesCount = Array.isArray(answerTypes) ? answerTypes.length : 0;
    console.log(`Validate number of answer types matches number of registration questions - found ${questionsCount} questions and ${answerTypesCount} answer types`);
    if (answerTypesCount !== questionsCount) {
      throw new Error('Number of answer types does not match number of registration questions');
    }
  }

  /**
   * Validates that a specific answer type is supported
   * @param answerType answer type to validate (null/falsy, 'F', or 'U')
   * @param questionLabel label of the question being answered
   */
  validateAnswerTypeIsSupported(answerType?: string | null, questionLabel?: string): void {
    console.log(`Validate answer type is supported for question: "${questionLabel}" (Type: ${answerType})`);
    const supportedTypes = [null, undefined, '', 'F', 'U'];
    if (!supportedTypes.includes(answerType ?? null)) {
      throw new Error(`Unsupported registration question answer type: ${answerType} (question: "${questionLabel}")`);
    }
  }

}
