import { z } from 'zod';

const getSchema = (
  type: string,
  id: string,
  errorLabel: string | undefined,
  mandatoryErrorLabel: string | undefined,
  requireValidation?: boolean
) => {
  if (type === 'select') {
    return {
      [id]: z.object({
        displayValue: z.string().min(1, errorLabel),
        value: z.string(),
      }),
    };
  } else {
    return {
      [id]: requireValidation ? z.string().min(1, mandatoryErrorLabel) : z.string().optional(),
    };
  }
};

const getInitialValue = (
  id: string,
  type: string,
  initialValue: string,
  options: string[] | null,
  hideInitialValue?: boolean
) => {
  if (type === 'select') {
    return {
      [id]: {
        displayValue: hideInitialValue ? '' : (options?.[parseInt(initialValue) - 1] ?? ''),
        value: hideInitialValue ? '' : (initialValue ?? ''),
      },
    };
  } else {
    return {
      [id]: hideInitialValue ? '' : initialValue,
    };
  }
};

export const defaultQuestionsAndSchema = (
  questions: any,
  errorLabel: string,
  mandatoryErrorLabel: string,
  displayMandatory?: boolean,
  hideInitialValue?: boolean
) => {
  let requireValidation;
  let questionsSchemaObj = {},
    defaultQuestionsObj = {};
  questions &&
    questions?.forEach((question: any) => {
      if (displayMandatory && question?.mandatory) {
        requireValidation = true;
      } else {
        requireValidation = false;
      }

      questionsSchemaObj = {
        ...questionsSchemaObj,
        ...getSchema(
          question.type,
          question.id,
          errorLabel,
          mandatoryErrorLabel,
          requireValidation
        ),
      };
      defaultQuestionsObj = {
        ...defaultQuestionsObj,
        ...getInitialValue(
          question.id,
          question.type,
          question.answer,
          question.options,
          hideInitialValue
        ),
      };
    });

  return { questionsSchemaObj, defaultQuestionsObj };
};
