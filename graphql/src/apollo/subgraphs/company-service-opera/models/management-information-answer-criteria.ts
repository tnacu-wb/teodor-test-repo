export class ManagementInformationAnswer {
  answerType?: AnswerType;
  answers: string[];

  constructor(data: any) {
    this.answerType = data.answerType;
    this.answers = data.answers;
  }
}
