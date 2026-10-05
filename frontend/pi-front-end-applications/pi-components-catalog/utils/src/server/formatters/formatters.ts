import {
  BBAnswerType,
  UserDefinedQuestionAnswered,
  EmployeeStatus,
  QuestionLocationType,
  AddressInfo,
  PAYMENT_TYPES,
} from '@whitbread-eos/api';
import type { SwitchState } from '@whitbread-eos/api';
import sanitizeHtml from 'sanitize-html';

import { isPIBACardType } from '../validators/index';

export const ParseDateToYMD = (date: Date | undefined) => {
  if (!date) {
    return;
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export const parseRegistrationQuestions = (rawQuestions: Record<string, any>) => {
  const questionsArray = [];

  const fillQuestionObject = (
    id: string,
    mandatory: boolean,
    type: BBAnswerType | null,
    options: string[] | undefined,
    answer: string | undefined,
    label: string,
    key?: string
  ) => {
    return {
      id: key ?? id,
      mandatory,
      type: type === BBAnswerType.U ? 'select' : 'text',
      options,
      answer: answer ?? '',
      label,
    };
  };
  if (
    rawQuestions?.purchaseOrderManagement &&
    rawQuestions?.purchaseOrderManagement.active &&
    rawQuestions?.purchaseOrderManagement.location === QuestionLocationType.R
  ) {
    questionsArray.push(
      fillQuestionObject(
        rawQuestions.purchaseOrderManagement.questionId,
        rawQuestions.purchaseOrderManagement.mandatory,
        rawQuestions.purchaseOrderManagement.managementInformationAnswer?.answerType,
        rawQuestions.purchaseOrderManagement.managementInformationAnswer?.answers,
        rawQuestions.purchaseOrderManagement.answer,
        rawQuestions.purchaseOrderManagement.label,
        'purchaseOrderAnswer'
      )
    );
  }
  if (
    rawQuestions?.customerReferenceManagement &&
    rawQuestions?.customerReferenceManagement.active &&
    rawQuestions?.customerReferenceManagement.location === QuestionLocationType.R
  ) {
    questionsArray.push(
      fillQuestionObject(
        rawQuestions.customerReferenceManagement.questionId,
        rawQuestions.customerReferenceManagement.mandatory,
        rawQuestions.customerReferenceManagement.managementInformationAnswer?.answerType,
        rawQuestions.customerReferenceManagement.managementInformationAnswer?.answers,
        rawQuestions.customerReferenceManagement.answer,
        rawQuestions.customerReferenceManagement.label,
        'customerReferenceAnswer'
      )
    );
  }

  if (
    rawQuestions?.userDefinedQuestions?.length &&
    rawQuestions?.userDefinedQuestions[0] !== null
  ) {
    rawQuestions.userDefinedQuestions.map((question: UserDefinedQuestionAnswered) => {
      if (question?.location === QuestionLocationType.R) {
        questionsArray.push(
          fillQuestionObject(
            question.questionId ?? '',
            question.mandatory,
            question.managementInformationAnswer?.answerType ?? BBAnswerType.F,
            question.managementInformationAnswer.answers,
            question.userDefinedAnswer,
            question.label
          )
        );
      }
    });
  }

  if (
    rawQuestions?.userDefinedManagement?.length &&
    rawQuestions?.userDefinedManagement[0] !== null
  ) {
    rawQuestions.userDefinedManagement.map((question: UserDefinedQuestionAnswered) => {
      if (question?.location === QuestionLocationType.R) {
        questionsArray.push(
          fillQuestionObject(
            question.questionId ?? '',
            question.mandatory,
            question.managementInformationAnswer?.answerType ?? BBAnswerType.F,
            question.managementInformationAnswer.answers,
            question.userDefinedAnswer,
            question.label
          )
        );
      }
    });
  }

  return questionsArray;
};

export function formatIBAssetsUrl(path = '/') {
  const checkedPath = path.includes('/') ? path : '/';
  return `https://${process.env.NEXT_PUBLIC_ASSETS_URL}${checkedPath}`;
}

export function formatHDPUrl(redirectURL: string): string {
  if (!redirectURL || redirectURL.indexOf('&') === -1) {
    return redirectURL;
  }

  const checkNoOfPairs = redirectURL.match(/&/g) || [];
  const checkNoOfParams = redirectURL.match(/=/g) || [];
  if (checkNoOfPairs?.length === 0 || checkNoOfParams?.length !== checkNoOfPairs?.length + 1) {
    return redirectURL;
  }

  return redirectURL
    .split('&')
    .map((kvPair: string) => {
      if (kvPair.indexOf('ARRdd') > -1 || kvPair.indexOf('ARRmm') > -1) {
        const [key, value] = kvPair.split('=');
        return `${key}=${('0' + value).slice(-2)}`;
      }
      return kvPair;
    })
    .join('&');
}

export const capitalizeFirstLetter = (input: string): string => {
  const lowerCase = input.toLowerCase();
  return lowerCase.charAt(0).toUpperCase() + lowerCase.slice(1);
};

export function getLabelType(status: string | undefined, isEmployeeStatus = false): string {
  switch (status) {
    case EmployeeStatus.Active:
      return isEmployeeStatus
        ? 'userMgmt.account.status.active'
        : 'userMgmt.manageEmployees.seachresults.status.active';
    case EmployeeStatus.Inactive:
      return isEmployeeStatus
        ? 'userMgmt.account.status.inactive'
        : 'userMgmt.manageEmployees.seachresults.status.inactive';
    case EmployeeStatus.Suspended:
    case EmployeeStatus.Purged:
    case EmployeeStatus.Deactivated:
      return isEmployeeStatus
        ? 'userMgmt.account.status.deactivated'
        : 'userMgmt.manageEmployees.seachresults.status.deactivated';
    default:
      return '';
  }
}

export function getStyling(status: EmployeeStatus): string {
  switch (status) {
    case EmployeeStatus.Active:
      return 'bg-success';
    case EmployeeStatus.Inactive:
      return 'bg-warning';
    case EmployeeStatus.Suspended:
    case EmployeeStatus.Purged:
    case EmployeeStatus.Deactivated:
      return 'bg-error';
    default:
      return '';
  }
}

export function resolveAndDownloadBlob(response: Blob, name: string, format: string) {
  let filename = `${name}${format}`;
  filename = decodeURI(filename);
  const url = window.URL.createObjectURL(new Blob([response]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  window.URL.revokeObjectURL(url);
  link.remove();
}

export function formatAccountNumber(accountNumber: string) {
  return accountNumber ? accountNumber.replace(/(\d{4})(?=\d)/g, '$1 ') : '';
}

const switchStateMapping: Record<string, string[]> = {
  premierInnBreakfast: ['11'],
  continentalBreakfast: ['12'],
  freeChildBreakfast: ['15'],
  mealDeal: ['17'],
  hubBreakfast: ['18'],
  ultimateWifi: ['135', '136', '137'],
};

export const mapSwitchState = (switchState: SwitchState): string[] => {
  const upsellItemsAllowed: string[] = [];

  Object.entries(switchState).forEach(([key, value]) => {
    if (value) {
      upsellItemsAllowed.push(...(switchStateMapping[key] || []));
    }
  });

  return upsellItemsAllowed;
};

export const getDefaultSwitchState = (upsellItemsAllowed: string[]): SwitchState => {
  const defaultState: SwitchState = {
    premierInnBreakfast: false,
    continentalBreakfast: false,
    mealDeal: false,
    hubBreakfast: false,
    ultimateWifi: false,
  };

  Object.entries(switchStateMapping).forEach(([key, values]) => {
    if (values.some((value) => upsellItemsAllowed.includes(value))) {
      defaultState[key as keyof SwitchState] = true;
    }
  });

  return defaultState;
};

export const getVariant = (field: string, variant?: string) =>
  variant ? `${variant}.${field}` : field;

export function normalizeAddress(address: AddressInfo): AddressInfo {
  return Object.fromEntries(
    Object.entries(address).map(([key, value]) => [key, value === null ? '' : value])
  ) as AddressInfo;
}

export function findError(path: string, obj: any) {
  if (!obj || Object.keys(obj).length === 0) {
    return undefined;
  }

  const keys = path.split('.');
  let current = obj;

  for (const key of keys) {
    if (current[key] === undefined) {
      return undefined;
    }
    current = current[key];
  }

  return current?.message;
}

export const getSavedCardType = (cardType = '') => {
  if (!cardType) return undefined;
  return isPIBACardType(cardType) ? PAYMENT_TYPES.KEEP_PIBA : PAYMENT_TYPES.KEEP_CARD;
};

export const sanitize = (html: string) => {
  return sanitizeHtml(html, {
    allowedTags: [],
    allowedAttributes: {},
  });
};

const parseUserDefinedAnswer = (
  data: Record<string, string | Record<string, string>>,
  key: string
) => {
  return typeof data[key] === 'string' ? data[key] : data[key]?.value;
};

export const parseAnswersObj = (data: Record<string, any>, excludeKeys?: string[]) => {
  const answersObj = {
    userDefinedAnswers: Object.keys(data)
      .filter((key: string) => !excludeKeys?.includes(key))
      .map((key: string) => ({
        miID: key,
        miAnswer: parseUserDefinedAnswer(data, key),
      })),
  } as Record<string, string | Record<string, string>[]>;
  if ('customerReferenceAnswer' in data) {
    answersObj.customerReferenceAnswer = data.customerReferenceAnswer as string;
  }
  if ('purchaseOrderAnswer' in data) {
    answersObj.purchaseOrderAnswer = data.purchaseOrderAnswer as string;
  }

  return answersObj;
};
