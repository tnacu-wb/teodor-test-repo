export type EmployeeQuestionType = 'select' | 'text';
export type RegistrationQuestionWithAnswer = {
  id: string;
  mandatory: boolean;
  type: EmployeeQuestionType;
  options: string[] | null;
  answer: string;
  label: string;
};
