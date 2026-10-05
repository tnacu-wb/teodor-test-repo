'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  cardDisplayNameOptions,
  cardUserType,
  FormInnB,
  Language,
  LOCALES,
  OptionType,
  requestStatus,
  UserAccessLevels,
  URLParams,
} from '@whitbread-eos/api';
import { Notification } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardFooter, WizardPage } from '@whitbread-eos/layout';
import { getAuthCookie } from '@whitbread-eos/utils';
import {
  useTranslation,
  getPathForLocale,
  ParseDateToYMD,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils';
import {
  addPayAppCard,
  IBPayCardUserSchema,
  IBPayCardLimitsSchema,
} from '@whitbread-eos/utils/server';
import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';
import { FormProvider, useForm } from 'react-hook-form';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges/index';
import {
  CardUserForm,
  parseDisplayName,
} from '~components/innBusiness/forms/AddCardForms/CardUserForm';

import { Analytics } from '../analytics/analytics';
import { PayApplicationState } from '../types';

type Props = {
  locale: LOCALES;
  cardHolderName: string;
  companyId: string;
  language: Language;
  calendarLabels: FormInnB | Record<string, never>;
  accessLevel: UserAccessLevels;
  loggedEmployeeDetails: Record<string, string>;
  isCurrentUserInitiator: boolean;
};

export function AddPayAppCard({
  locale,
  cardHolderName,
  companyId,
  language,
  calendarLabels,
  accessLevel,
  loggedEmployeeDetails,
  isCurrentUserInitiator,
}: Readonly<Props>) {
  const router = useRouter();
  const token = getAuthCookie();
  const searchParams = useSearchParams();
  const urlEmployeeId = searchParams?.get(URLParams.employeeId);
  const { goToPreviousStep, setWizardState } = useWizardContext<PayApplicationState>();
  const { t } = useTranslation(['payApplication', 'cards']);
  const { wizardState, icons } = useWizardContext<PayApplicationState>();
  const [isAddingCard, setIsAddingCard] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState<OptionType | null>(null);
  const [showFailError, setShowFailError] = useState(false);
  const initiatorEmail = wizardState?.contactDetails?.email;
  const schema = IBPayCardUserSchema(t).schema.merge(IBPayCardLimitsSchema(t).schema);

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      user: cardUserType.me,
      employeeId: loggedEmployeeDetails.employeeId,
      cardDisplayNameOption: {
        value: cardDisplayNameOptions.fullName,
        displayValue: t('cards.cardMgmt.addCard.displayName.options.fullName'),
      },
      cardDisplayName: '',
      creditLimit: false,
      creditLimitNumber: '',
      usageRestriction: false,
      startDate: undefined,
      endDate: undefined,
    },
  });

  const {
    getValues,
    setError,
    clearErrors,
    watch,
    formState: { errors },
  } = formMethods;

  const employeeIdValue = watch('employeeId');

  const handleAddCard = async () => {
    const isValid =
      IBPayCardUserSchema(t).validation(getValues(), setError, clearErrors) &&
      IBPayCardLimitsSchema(t).validation(getValues(), setError, clearErrors);

    if (isValid) {
      setIsAddingCard(true);
      setShowFailError(false);
      const {
        title = '',
        firstName = '',
        lastName = '',
        emailAddress = '',
        employeeIdNumber = '',
      } = selectedEmployee?.employeeData ?? {};
      const isCardForCurrentUser = getValues('user') === cardUserType.me;
      const isInitiatorsCard =
        (isCardForCurrentUser && isCurrentUserInitiator) ||
        (!isCardForCurrentUser && initiatorEmail === emailAddress);
      const isCustomName =
        getValues('cardDisplayNameOption').value === cardDisplayNameOptions.custom;
      const cardName = isCustomName
        ? getValues('cardDisplayName')
        : parseDisplayName(getValues('cardDisplayNameOption').value, title, firstName, lastName);
      const response = await addPayAppCard(
        wizardState?.applicationGuid,
        wizardState?.applicationId,
        wizardState?.scheme,
        {
          cardName,
          emailAddress,
          foreName: firstName,
          lastName,
          myCard: isInitiatorsCard,
          title,
          restrictCardUsage: getValues('usageRestriction'),
          startDate: getValues('usageRestriction') ? ParseDateToYMD(getValues('startDate')) : null,
          endDate: getValues('usageRestriction') ? ParseDateToYMD(getValues('endDate')) : null,
          cardLimit: getValues('creditLimit') ? Number(getValues('creditLimitNumber')) : null,
        },
        token,
        isInitiatorsCard
          ? undefined
          : isCardForCurrentUser
            ? Number(loggedEmployeeDetails?.employeeIdNumber)
            : Number(employeeIdNumber)
      );
      if (response?.status === requestStatus.success) {
        setIsAddingCard(false);
        await setWizardState((prev) => ({
          ...prev,
          cardDetails: [
            ...prev.cardDetails,
            {
              myCard: isInitiatorsCard,
              cardName,
              cardOwnerName: `${title} ${firstName} ${lastName}`,
              emailAddress: emailAddress,
              cardGuid: response?.cardGuid,
            },
          ],
        }));
        goToPreviousStep();
      } else {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        setShowFailError(true);
        setIsAddingCard(false);
      }
    }
  };

  const handleSaveAndClose = () => {
    router.push(
      getPathForLocale(
        locale,
        `business-pay/pay-application-save?applicationGuid=${wizardState.applicationGuid}&applicationId=${wizardState.applicationId}`
      )
    );
  };

  return (
    <FormProvider {...formMethods}>
      <WizardPage
        type="form"
        formTitle={t('payApplication.card.add.forMyself.title')}
        showBackButton={true}
        onBackClick={goToPreviousStep}
        footer={
          <WizardFooter
            linkLabel={t('payApplication.companyDetails.closeOut')}
            buttonLabel={t('payApplication.card.add.addCard')}
            onButtonClick={handleAddCard}
            onLinkClick={handleSaveAndClose}
            linkDisabled={isAddingCard}
            buttonDisabled={isAddingCard || !employeeIdValue}
          />
        }
      >
        {showFailError && (
          <Notification
            type="error"
            className="mt-4"
            icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            title={t('payApplication.application.sent.failed.message')}
            message={t('payApplication.application.sent.error')}
          />
        )}
        <CardUserForm
          token={token}
          icons={icons}
          cardHolderName={cardHolderName}
          companyId={companyId}
          language={language}
          calendarLabels={calendarLabels}
          accessLevel={accessLevel}
          defaultEmployeeId={loggedEmployeeDetails.employeeId}
          updateSelectedEmployee={(value: OptionType) => setSelectedEmployee(value)}
          hideContinueButton={true}
          hideAddEmployeeButton={false}
          loggedEmployeeDetails={loggedEmployeeDetails}
          isPayApp={true}
          applicationId={wizardState?.applicationId}
          applicationGuid={wizardState?.applicationGuid}
        />
        {!isAddingCard && !urlEmployeeId && <ReviewChanges />}
        <Analytics pageName="Pay Application: Card Details" errors={errors} />
      </WizardPage>
    </FormProvider>
  );
}
