export type CompanyManagementDetails = {
  customerReferenceManagement: Questions;
  purchaseOrderManagement: Questions;
  userDefinedManagement: Array<Questions>;
};

export type ReferencesQuestions = {
  questionId?: number;
  mandatory?: boolean;
  question?: string;
  hasError?: boolean;
  answer?: string;
  answerType?: string;
  dirty?: boolean;
  managementHeader?: string;
};

export type Questions = {
  questionId?: number;
  active?: boolean;
  label?: string;
  location?: string;
  managementHeader?: string;
  managementInformationAnswer?: ManagementInformationAnswer;
  mandatory?: boolean;
  question?: string;
  hasError?: boolean;
  answer?: string;
};

export type ManagementInformationAnswer = {
  answerType?: string;
  answers?: Array<string>;
};
