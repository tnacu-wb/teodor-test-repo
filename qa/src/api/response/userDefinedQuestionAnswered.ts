/**
 * {
       questionId: 'COQU_546b8cc3-fec6-446f-b84e-d81d3e5bc',
       label: 'What type of car do you drive?',
       mandatory: false,
       managementHeader: 'car transmission',
       active: true,
       location: 'R',
       managementInformationAnswer: [Object],
       type: null,
       positionId: 1,
       userDefinedAnswer: null
     }
 */
export class UserDefinedQuestionAnswered {
  [key: string]: unknown;
  active?: boolean;
  label?: string;
  location?: string;
  managementHeader?: string;
  managementInformationAnswer?: { answerType?: string | null; answers?: string[] | null };
  mandatory?: boolean;
  positionId?: number;
  questionId?: string;
  type?: string | null;
  userDefinedAnswer?: string | null;

  constructor(data: { userDefinedQuestionAnswered?: Record<string, unknown> } = {}) {
    const userDefinedQuestionAnswered = data.userDefinedQuestionAnswered ?? {};
    this.questionId = userDefinedQuestionAnswered.questionId as string | undefined;
    this.label = userDefinedQuestionAnswered.label as string | undefined;
    this.mandatory = userDefinedQuestionAnswered.mandatory as boolean | undefined;
    this.managementHeader = userDefinedQuestionAnswered.managementHeader as string | undefined;
    this.active = userDefinedQuestionAnswered.active as boolean | undefined;
    this.location = userDefinedQuestionAnswered.location as string | undefined;
    this.managementInformationAnswer = userDefinedQuestionAnswered.managementInformationAnswer as UserDefinedQuestionAnswered['managementInformationAnswer'];
    this.type = userDefinedQuestionAnswered.type as string | null | undefined;
    this.positionId = userDefinedQuestionAnswered.positionId as number | undefined;
    this.userDefinedAnswer = userDefinedQuestionAnswered.userDefinedAnswer as string | null | undefined;
  }

  static fromResponse(data: { userDefinedQuestionAnswered?: Record<string, unknown> }): UserDefinedQuestionAnswered {
    return new UserDefinedQuestionAnswered(data);
  }
}
