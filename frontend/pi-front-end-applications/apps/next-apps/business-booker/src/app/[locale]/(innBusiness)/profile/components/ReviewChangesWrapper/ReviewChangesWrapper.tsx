'use client';

import { LOCALES, Customer, Language, RegistrationQuestionWithAnswer } from '@whitbread-eos/api';
import React, { useState } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { ChangePassword } from '../ChangePassword/ChangePassword';
import { CompanyRegistrationQuestions } from '../CompanyRegistrationQuestions/CompanyRegistrationQuestions';
import { MealsAndExtras } from '../MealsAndExtras/MealsAndExtras';
import { PaymentSection } from '../PaymentType';
import { RoomPreferences } from '../RoomPreferences/RoomPreferences';
import { YourProfile } from '../YourProfile/YourProfile';

type Props = {
  baseDataTestId?: string;
  icons: Record<string, string>;
  profileDetails: Customer;
  locale: LOCALES;
  hasPaymentCard: boolean;
  token: string;
  employeeId: string;
  language: Language;
  companyRegistrationQuestions: RegistrationQuestionWithAnswer[];
  employeeDetails: Record<string, Record<string, string>>;
  companyId: string;
  isBusinessPayRole: boolean;
};

export enum CHANGES_TRACKER {
  CHANGE_PASSWORD = 'changePassword',
  YOUR_PROFILE = 'yourProfile',
  ROOM_PREFERENCES = 'roomPreferences',
  PAYMENT_TYPE = 'paymentType',
  REGISTRATION_QUESTIONS = 'registrationQuestions',
  MEALS_EXTRAS = 'mealsExtras',
}

export function ReviewChangesWrapper({
  baseDataTestId,
  icons,
  profileDetails,
  locale,
  hasPaymentCard,
  token,
  employeeId,
  language,
  companyRegistrationQuestions,
  employeeDetails,
  companyId,
  isBusinessPayRole,
}: Props) {
  const defaultValues = {
    isEditable: false,
    isUpdating: false,
    reviewChanges: false,
    isDirty: false,
  };
  const [actualState, setActualState] = useState({
    [CHANGES_TRACKER.CHANGE_PASSWORD]: defaultValues,
    [CHANGES_TRACKER.YOUR_PROFILE]: defaultValues,
    [CHANGES_TRACKER.ROOM_PREFERENCES]: defaultValues,
    [CHANGES_TRACKER.PAYMENT_TYPE]: defaultValues,
    [CHANGES_TRACKER.REGISTRATION_QUESTIONS]: defaultValues,
    [CHANGES_TRACKER.MEALS_EXTRAS]: defaultValues,
  });

  const handleDiscardChanges = () => {
    const componentWithReviewChangesOpen = Object.entries(actualState).find(
      ([, state]) => state.reviewChanges
    );
    if (componentWithReviewChangesOpen) {
      const [area] = componentWithReviewChangesOpen;
      setActualState((prevState) => ({
        ...prevState,
        [area]: defaultValues,
      }));
    }
  };

  const handleContinue = () => {
    const componentWithReviewChangesOpen = Object.entries(actualState).find(
      ([, state]) => state.reviewChanges
    );
    if (componentWithReviewChangesOpen) {
      const [area] = componentWithReviewChangesOpen;
      setActualState((prevState) => ({
        ...prevState,
        [area]: {
          ...prevState[area as CHANGES_TRACKER],
          reviewChanges: false,
        },
      }));
    }
  };

  const handleReviewChangesToggle = (area: CHANGES_TRACKER, isOpen: boolean) => {
    setActualState((prevState) => ({
      ...prevState,
      [area]: {
        ...prevState[area as CHANGES_TRACKER],
        reviewChanges: isOpen,
      },
    }));
  };

  const handleIsEditableToggle = (area: CHANGES_TRACKER, value: boolean) => {
    setActualState((prevState) => ({
      ...prevState,
      [area]: {
        ...prevState[area as CHANGES_TRACKER],
        isEditable: value,
      },
    }));
  };

  const handleIsUpdateInProgressToggle = (area: CHANGES_TRACKER, value: boolean) => {
    setActualState((prevState) => ({
      ...prevState,
      [area]: {
        ...prevState[area],
        isUpdating: value,
      },
    }));
  };

  const handleIsDirtyToggle = (area: CHANGES_TRACKER, value: boolean) => {
    setActualState((prevState) => ({
      ...prevState,
      [area]: {
        ...prevState[area as CHANGES_TRACKER],
        isDirty: value,
      },
    }));
  };

  const shouldShowReviewChanges =
    Object.values(actualState).some((state) => state.isEditable) &&
    Object.values(actualState).some((state) => state.isDirty) &&
    !Object.values(actualState).some((state) => state.isUpdating);

  const handlersProps = {
    onReviewChangesToggle: handleReviewChangesToggle,
    onIsEditableToggle: handleIsEditableToggle,
    onIsUpdatingToggle: handleIsUpdateInProgressToggle,
    onIsDirtyToggle: handleIsDirtyToggle,
  };

  return (
    <>
      <YourProfile
        icons={icons}
        baseDataTestId={baseDataTestId}
        profileDetails={profileDetails}
        locale={locale}
        {...actualState[CHANGES_TRACKER.YOUR_PROFILE]}
        {...handlersProps}
      />
      <ChangePassword
        icons={icons}
        baseDataTestId={baseDataTestId}
        locale={locale}
        profileDetails={profileDetails}
        {...actualState[CHANGES_TRACKER.CHANGE_PASSWORD]}
        {...handlersProps}
      />

      {/* start - hide sections for Business Pay users */}
      {!isBusinessPayRole && (
        <>
          {companyRegistrationQuestions && (
            <div data-testid={`${baseDataTestId}-Company-Registration-Questions-Container`}>
              <CompanyRegistrationQuestions
                companyRegistrationQuestions={companyRegistrationQuestions}
                baseDataTestId={baseDataTestId}
                icons={icons}
                employeeDetails={employeeDetails}
                language={language}
                companyId={companyId}
                {...actualState[CHANGES_TRACKER.REGISTRATION_QUESTIONS]}
                {...handlersProps}
              />
            </div>
          )}
          <PaymentSection
            hasPaymentCard={hasPaymentCard}
            icons={icons}
            profileDetails={profileDetails}
            token={token}
            employeeId={employeeId}
            language={language}
            locale={locale}
            {...actualState[CHANGES_TRACKER.PAYMENT_TYPE]}
            {...handlersProps}
          />
          <RoomPreferences
            baseDataTestId={baseDataTestId}
            icons={icons}
            profileDetails={profileDetails}
            {...actualState[CHANGES_TRACKER.ROOM_PREFERENCES]}
            {...handlersProps}
          />
          <MealsAndExtras
            baseDataTestId={baseDataTestId ?? ''}
            icons={icons}
            profileDetails={profileDetails}
            {...actualState[CHANGES_TRACKER.MEALS_EXTRAS]}
            {...handlersProps}
          />
        </>
      )}
      {/* End - hide sections for Business Pay users */}

      {shouldShowReviewChanges ? (
        <ReviewChanges
          isOpen={Object.values(actualState).some((state) => state.reviewChanges)}
          onDiscard={handleDiscardChanges}
          onContinue={handleContinue}
        />
      ) : (
        <></>
      )}
    </>
  );
}
