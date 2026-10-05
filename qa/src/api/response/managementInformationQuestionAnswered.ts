/**
 *
   {
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
}
 */
export class ManagementInformationQuestionAnswered {
  [key: string]: unknown;
  active?: boolean;
  answer?: string;
  label?: string;
  location?: string;
  managementHeader?: string;
  managementInformationAnswer?: { answerType?: string | null; answers?: string[] | null };
  mandatory?: boolean;
  positionId?: number;
  questionId?: string;
  type?: string | null;

  constructor(data: { managementInformationQuestionAnswered?: Record<string, unknown> } = {}) {
    const managementInformationQuestionAnswered = data.managementInformationQuestionAnswered ?? {};
    this.questionId = managementInformationQuestionAnswered.questionId as string | undefined;
    this.label = managementInformationQuestionAnswered.label as string | undefined;
    this.mandatory = managementInformationQuestionAnswered.mandatory as boolean | undefined;
    this.managementHeader = managementInformationQuestionAnswered.managementHeader as string | undefined;
    this.active = managementInformationQuestionAnswered.active as boolean | undefined;
    this.location = managementInformationQuestionAnswered.location as string | undefined;
    this.managementInformationAnswer = managementInformationQuestionAnswered.managementInformationAnswer as ManagementInformationQuestionAnswered['managementInformationAnswer'];
    this.type = managementInformationQuestionAnswered.type as string | null | undefined;
    this.positionId = managementInformationQuestionAnswered.positionId as number | undefined;
    this.answer = managementInformationQuestionAnswered.answer as string | undefined;
  }

  static fromResponse(data: { managementInformationQuestionAnswered?: Record<string, unknown> }): ManagementInformationQuestionAnswered {
    return new ManagementInformationQuestionAnswered(data);
  }
}
