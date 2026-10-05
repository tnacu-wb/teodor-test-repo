import { Box, BoxProps, Text, TextProps } from '@chakra-ui/react';
import {
  AnswerType,
  CompanyManagementDetails,
  QuestionLocation,
  Questions,
  ReferencesQuestions,
} from '@whitbread-eos/api';
import { Dropdown, FORM_VALIDATIONS, Input } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

interface Props {
  companyManagementDetails: CompanyManagementDetails;
  setPurchaseOrder: Dispatch<SetStateAction<ReferencesQuestions>>;
  purchaseOrder: ReferencesQuestions;
  setCustomerRef: Dispatch<SetStateAction<ReferencesQuestions>>;
  customerRef: ReferencesQuestions;
  setUserDefinedQuestions: Dispatch<SetStateAction<any>>;
  userDefinedQuestions: Array<any>;
  setValidateQuestions: Dispatch<SetStateAction<boolean>>;
  validateQuestions: boolean;
  setHasEmployeeQuestionsErrors: Dispatch<SetStateAction<boolean>>;
}

export default function EmployeeQuestions({
  companyManagementDetails,
  setPurchaseOrder,
  purchaseOrder,
  setCustomerRef,
  customerRef,
  setUserDefinedQuestions,
  userDefinedQuestions,
  setValidateQuestions,
  validateQuestions,
  setHasEmployeeQuestionsErrors,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const baseDataTestId = 'EmployeeQuestions';
  const { userDefinedManagement, customerReferenceManagement, purchaseOrderManagement } =
    companyManagementDetails || {};
  const purchaseOrderQuestion =
    purchaseOrderManagement?.active &&
    purchaseOrderManagement?.location === QuestionLocation.BOOKING;
  const customerRefQuestion =
    customerReferenceManagement?.active &&
    customerReferenceManagement?.location === QuestionLocation.BOOKING;
  const [errorStyling, setErrorStyling] = useState(false);
  const trimAnswerValue = (value: unknown) => (typeof value === 'string' ? value.trim() : '');

  const userDefinedQuestionsWithID = userDefinedManagement?.map(
    (question: Questions, idx: number) => {
      return question['questionId'] ? { ...question } : { ...question, questionId: idx };
    }
  );

  const errorOnPage = () => {
    return (
      (userDefinedQuestions.some((item) => item.hasError) ||
        purchaseOrder?.hasError ||
        customerRef?.hasError) ??
      false
    );
  };

  useEffect(() => {
    const defaultState = userDefinedQuestionsWithID
      .filter((item: Questions) => item.active && item.location === QuestionLocation.BOOKING)
      .map((i) => {
        return {
          answer: '',
          question: i?.label,
          questionId: i?.questionId,
          mandatory: i?.mandatory,
          hasError: false,
          managementHeader: i?.managementHeader,
        };
      });
    setHasEmployeeQuestionsErrors(errorOnPage);
    setUserDefinedQuestions([...defaultState]);
  }, []);

  useEffect(() => {
    validateQuestionsHandler();
    setHasEmployeeQuestionsErrors(errorOnPage);
    purchaseOrder.dirty = questionIsDirty(purchaseOrder);
    customerRef.dirty = questionIsDirty(customerRef);
  }, [userDefinedQuestions, validateQuestions, purchaseOrder, customerRef]);

  const setUserDefinedQuestionsChange = (value: Questions) => {
    const filteredAnswers = userDefinedQuestions.filter(
      (item) => item.questionId !== value.questionId
    );

    const newQuestion = {
      answer: value?.label as string,
      question: value?.question,
      questionId: value?.questionId,
      mandatory: value?.mandatory,
      managementHeader: value?.managementHeader,
    };

    const questionExist = userDefinedQuestions.some((item) => item.questionId === value.questionId);
    const updatedQuestions = questionExist
      ? [...filteredAnswers, newQuestion]
      : [...userDefinedQuestions, newQuestion];
    setUserDefinedQuestions(updatedQuestions);
  };

  const setUserDefinedQuestionsChangeInput = (
    label: string,
    question: string,
    questionId: number,
    mandatory: boolean,
    managementHeader: string
  ) => {
    setUserDefinedQuestionsChange({
      label,
      question,
      questionId,
      mandatory,
      managementHeader,
    });
  };

  const validationSteps = (errorItem: Questions) => {
    if (validateQuestions) {
      scrollToError(errorItem);
      setErrorStyling(true);
      setValidateQuestions(false);
    }
  };

  const errorList: Questions[] = [];
  const purchaseOrderKeys = Object.keys(purchaseOrder);
  const customerRefKeys = Object.keys(customerRef);

  const answerHasError = (item: Questions) => {
    const { mandatory } = item;
    const trimmedAnswer = trimAnswerValue(item?.answer as string);

    return (
      (trimmedAnswer === '' && mandatory) ||
      !FORM_VALIDATIONS.REFERENCES.MATCHES.test(trimmedAnswer)
    );
  };

  if (purchaseOrderQuestion) {
    purchaseOrder.question = purchaseOrderManagement.label;
    purchaseOrder.hasError = answerHasError(purchaseOrder);
    purchaseOrder.mandatory = purchaseOrderManagement.mandatory;
    purchaseOrder.managementHeader = purchaseOrderManagement.managementHeader;
  }

  if (customerRefQuestion) {
    customerRef.question = customerReferenceManagement.label;
    customerRef.hasError = answerHasError(customerRef);
    customerRef.mandatory = customerReferenceManagement.mandatory;
    customerRef.managementHeader = customerReferenceManagement.managementHeader;
  }

  const questionIsDirty = (item: Questions) => {
    const trimmedAnswer = trimAnswerValue(item?.answer as string);

    return trimmedAnswer !== '';
  };

  const errorMessage = (item: Questions) => {
    const { answer, question } = item || {};
    const trimmedAnswer = trimAnswerValue(answer as string);

    if (trimmedAnswer === '') {
      return t('config.errorMessages.refAnswer.required');
    } else if (
      question === purchaseOrderManagement.label &&
      !FORM_VALIDATIONS.REFERENCES.MATCHES.test(trimmedAnswer)
    ) {
      return t('config.errorMessages.cardDetails.purchaseOrder.valid');
    } else if (
      question === customerReferenceManagement.label &&
      !FORM_VALIDATIONS.REFERENCES.MATCHES.test(trimmedAnswer)
    ) {
      return t('paymentQuestions.refrenceValid');
    } else if (
      question !== purchaseOrderManagement.label &&
      question !== customerReferenceManagement.label &&
      !FORM_VALIDATIONS.CUSTOM_QUESTION.MATCHES.test(trimmedAnswer)
    ) {
      return t('employeeQuestions.customFreeTextRegex');
    }
  };

  const validateQuestionsHandler = () => {
    purchaseOrderQuestion &&
      purchaseOrderKeys?.forEach(() => {
        validationSteps(purchaseOrder);
      });

    customerRefQuestion &&
      customerRefKeys?.forEach(() => {
        validationSteps(customerRef);
      });

    userDefinedQuestions
      .sort((a, b) => (a.questionId > b.questionId ? 1 : -1))
      .forEach((item) => {
        Object.keys(item).forEach(() => {
          const trimmedAnswer = trimAnswerValue(item?.answer as string);
          item.hasError =
            (trimmedAnswer === '' && item.mandatory) ||
            !FORM_VALIDATIONS.CUSTOM_QUESTION.MATCHES.test(trimmedAnswer);

          validationSteps(item);
        });
      });
  };

  const scrollToError = (item: Questions) => {
    if (item?.hasError) {
      errorList.push(item);

      const firstElementId = errorList[0]?.questionId;
      const firstErrorElement =
        document.querySelector(`[data-testid="input-${baseDataTestId}-${firstElementId}"]`) ??
        document.querySelector(
          `[data-testid="DropdownComp-${baseDataTestId}-${firstElementId}-menuButton"]`
        );
      firstErrorElement?.scrollIntoView({ behavior: 'smooth' });
    }
  };

  userDefinedQuestions
    .sort((a, b) => (a.questionId > b.questionId ? 1 : -1))
    .forEach((item) => {
      Object.keys(item).forEach(() => {
        const { mandatory } = item;
        const trimmedAnswer = trimAnswerValue(item?.answer as string);
        item.hasError =
          (trimmedAnswer === '' && mandatory) ||
          !FORM_VALIDATIONS.CUSTOM_QUESTION.MATCHES.test(trimmedAnswer);
        item.touched = questionIsDirty(item);
      });
    });

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Container')}>
      <Text data-testid={formatDataTestId(baseDataTestId, 'Title')} {...titleStyle}>
        {t('booking.references')}
      </Text>

      <Text color="darkGrey1" mb="xl" data-testid={formatDataTestId(baseDataTestId, 'Description')}>
        {t('employeeQuestions.subTitle')}
      </Text>

      {purchaseOrderQuestion && (
        <Box {...employeeQuestionsWrapperStyle}>
          <Input
            data-testid={formatDataTestId(baseDataTestId, 'PurchaseOrderNumber')}
            label={`${purchaseOrderManagement?.label}${
              purchaseOrderManagement.mandatory ? '*' : ''
            }`}
            showLabel={true}
            name={`EmployeeQuestions-8`}
            placeholderText={`${purchaseOrderManagement?.label}${
              purchaseOrderManagement.mandatory ? '*' : ''
            }`}
            onChange={(val: string) => {
              setPurchaseOrder({
                ...purchaseOrder,
                answer: val,
                mandatory: purchaseOrderManagement.mandatory,
                managementHeader: purchaseOrderManagement.managementHeader,
              });
            }}
            value={purchaseOrder.answer}
            styles={inputStyles}
            error={
              ((answerHasError(purchaseOrder) && errorStyling) || questionIsDirty(purchaseOrder)) &&
              errorMessage(purchaseOrder)
            }
          />
        </Box>
      )}

      {customerRefQuestion && (
        <Box {...employeeQuestionsWrapperStyle}>
          <Input
            data-testid={formatDataTestId(baseDataTestId, 'CustomerOwnReference')}
            label={`${customerReferenceManagement?.label}${
              customerReferenceManagement.mandatory ? '*' : ''
            }`}
            showLabel={true}
            name={`EmployeeQuestions-9`}
            placeholderText={`${customerReferenceManagement?.label}${
              customerReferenceManagement.mandatory ? '*' : ''
            }`}
            onChange={(val: string) => {
              setCustomerRef({
                ...customerRef,
                answer: val,
                mandatory: customerReferenceManagement.mandatory,
                managementHeader: customerReferenceManagement.managementHeader,
              });
            }}
            value={customerRef.answer}
            styles={inputStyles}
            error={
              ((answerHasError(customerRef) && errorStyling) || questionIsDirty(customerRef)) &&
              errorMessage(customerRef)
            }
          />
        </Box>
      )}

      {userDefinedQuestionsWithID.map((item: Questions) => (
        <>
          {(item.active &&
            item?.location === QuestionLocation.BOOKING &&
            item?.managementInformationAnswer?.answerType === AnswerType.PRESET_ANSWER && (
              <Box key={item.questionId} {...employeeQuestionsWrapperStyle}>
                <Dropdown
                  dataTestId={`EmployeeQuestions-${item.questionId}`}
                  options={item?.managementInformationAnswer?.answers?.map(
                    (answer: string, idx: number) => {
                      return {
                        label: answer,
                        id: idx,
                        question: item.label,
                        questionId: item.questionId,
                        mandatory: item.mandatory,
                        managementHeader: item.managementHeader,
                      };
                    }
                  )}
                  onChange={(val: any) => {
                    setUserDefinedQuestionsChange(val);
                  }}
                  placeholder={`${item?.label}${item.mandatory ? '*' : ''}`}
                  dropdownStyles={{
                    menuButtonStyles: renderDropdownStyles(
                      Boolean(
                        userDefinedQuestions.find((i) => i.questionId === item.questionId)
                          ?.hasError && errorStyling
                      )
                    ),
                    menuListStyles: {
                      zIndex: 999999,
                    },
                  }}
                  hasError={Boolean(
                    userDefinedQuestions.find((i) => i.questionId === item.questionId)?.hasError &&
                    errorStyling
                  )}
                />
                {userDefinedQuestions.find((i) => i.questionId === item.questionId)?.hasError &&
                  errorStyling && (
                    <Box
                      {...formErrorStyles}
                      data-testid={formatDataTestId(baseDataTestId, 'ErrorContainer')}
                    >
                      <Box {...errorMessageStyles}>
                        <Text
                          {...errorTextStyles}
                          data-testid={formatDataTestId(baseDataTestId, 'ErrorText')}
                        >
                          {t('employeeQuestions.customDropdownMessage')}
                        </Text>
                      </Box>
                    </Box>
                  )}
              </Box>
            )) ||
            (item.active &&
              item?.location === QuestionLocation.BOOKING &&
              item?.managementInformationAnswer?.answerType === AnswerType.OWN_ANSWER && (
                <Box key={item.questionId} {...employeeQuestionsWrapperStyle}>
                  <Input
                    name={`EmployeeQuestions-${item.questionId}`}
                    label={`${item?.label}${item.mandatory ? '*' : ''}`}
                    placeholderText={`${item?.label}${item.mandatory ? '*' : ''}`}
                    styles={inputStyles}
                    onChange={(val: any) => {
                      setUserDefinedQuestionsChangeInput(
                        val,
                        item?.label as string,
                        item.questionId as number,
                        item.mandatory as boolean,
                        item.managementHeader as string
                      );
                    }}
                    error={
                      ((userDefinedQuestions.find((i) => i.questionId === item.questionId)
                        ?.hasError &&
                        errorStyling) ||
                        userDefinedQuestions.find((i) => i.questionId === item.questionId)
                          ?.touched) &&
                      errorMessage(
                        userDefinedQuestions.find((i) => i.questionId === item.questionId)
                      )
                    }
                  />
                </Box>
              ))}
        </>
      ))}
    </Box>
  );
}

const titleStyle = { fontSize: 'xl', fontWeight: 'semibold', mb: 'xl' };

const employeeQuestionsWrapperStyle = {
  w: {
    mobile: 'full',
    sm: '25.063rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mb: 'md',
};

const inputStyles = {
  inputElementStyles: {
    borderColor: 'lightGrey1',
  },
};

const renderDropdownStyles = (hasError: boolean) => {
  return {
    border: hasError ? '2px solid' : '1px solid',
    borderColor: hasError ? 'var(--chakra-colors-error)' : 'lightGrey1',
    borderRadius: 'var(--chakra-radii-md)',
  };
};

const formErrorStyles = {
  height: 'var(--chakra-space-lg)',
  position: 'relative',
} as BoxProps;

const errorMessageStyles = {
  position: 'absolute',
} as BoxProps;

const errorTextStyles = {
  color: 'error',
  fontSize: 'xs',
  marginLeft: 'md',
  marginTop: 'sm',
  whiteSpace: 'nowrap',
} as TextProps;
