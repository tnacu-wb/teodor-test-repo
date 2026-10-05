import { ManagementInformationAnswer } from './management-information-answer-criteria';

export class ManagementInformationQuestionCriteria {
  questionId?: string;
  label?: string;
  mandatory?: boolean;
  managementHeader?: string;
  active?: boolean;
  location: QuestionLocation;
  managementInformationAnswer: ManagementInformationAnswer;
  type?: string;
  positionId?: number;

  constructor(data: any) {
    this.questionId = data.questionId;
    this.label = data.label;
    this.mandatory = data.mandatory;
    this.managementHeader = data.managementHeader;
    this.active = data.active;
    this.location = data.location;
    this.managementInformationAnswer = data.managementInformationAnswer;
    this.type = data.type;
    this.positionId = data.positionId;
  }
}
