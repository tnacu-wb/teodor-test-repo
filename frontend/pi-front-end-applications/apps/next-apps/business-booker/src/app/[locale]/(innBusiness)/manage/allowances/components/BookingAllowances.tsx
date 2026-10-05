'use client';

import {
  Language,
  requestStatus,
  BookingAllowancesCriteria,
  FormData,
  Currency,
} from '@whitbread-eos/api';
import type { SwitchState } from '@whitbread-eos/api';
import { Button, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation, mapSwitchState } from '@whitbread-eos/utils';
import { updateBookingAllowances } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useRef, useState, useEffect, useCallback } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { BookingOptionsForm } from '~components/innBusiness/forms/BookingAllowancesForms/BookingOptionsForm';
import { IndividualPaymentCardsForm } from '~components/innBusiness/forms/BookingAllowancesForms/IndividualPaymentCardsForm';
import { PrePaidAllowancesForm } from '~components/innBusiness/forms/BookingAllowancesForms/PrePaidAllowancesForm';
import { SelectExtrasForm } from '~components/innBusiness/forms/BookingAllowancesForms/SelectExtrasForm';

type formType = {
  data: any;
  submittedForms: Record<string, boolean>;
};

type DirtySections = {
  bookingOptions: boolean;
  individualCards: boolean;
  prePaidAllowances: boolean;
  selectExtras: boolean;
};

type Props = {
  icons: Record<string, string>;
  language?: Language;
  companyId: string;
  bookingAllowances: BookingAllowancesCriteria;
};
const extrasCodesMapping: Record<string, string> = {
  premierInnBreakfast: '1',
  continentalBreakfast: '2',
  mealDeal: '3',
  hubBreakfast: '4',
  ultimateWifi: '5',
  bottleOfProsecco: '6',
};

export function BookingAllowances({ icons, bookingAllowances, companyId }: Props) {
  const baseDataTestId = 'BookingAllowances';
  const { t } = useTranslation(['company', 'users', 'profile']);
  const idTokenCookie = getAuthCookie();

  const { toast } = useToast();
  const router = useRouter();

  const bookingOptionsFormRef = useRef<HTMLFormElement | null>(null);
  const individualCardsFormRef = useRef<HTMLFormElement | null>(null);
  const prePaidAllowancesFormRef = useRef<HTMLFormElement | null>(null);
  const selectExtrasFormRef = useRef<HTMLFormElement | null>(null);

  const [formState, setFormState] = useState<formType>({
    data: bookingAllowances,
    submittedForms: {
      bookingOptions: false,
      individualCards: false,
      prePaidAllowances: false,
      selectExtras: false,
    },
  });
  const [dirtyBaselineAllowances, setDirtyBaselineAllowances] =
    useState<BookingAllowancesCriteria>(bookingAllowances);
  const [isUpdating, setIsUpdating] = useState(false);
  const [dirtySections, setDirtySections] = useState<DirtySections>({
    bookingOptions: false,
    individualCards: false,
    prePaidAllowances: false,
    selectExtras: false,
  });

  const isFormDirty = Object.values(dirtySections).some(Boolean);

  const handleDirtyChange = useCallback((section: keyof DirtySections, isDirty: boolean) => {
    setDirtySections((prev) => {
      if (prev[section] === isDirty) {
        return prev;
      }

      return {
        ...prev,
        [section]: isDirty,
      };
    });
  }, []);

  const handleBookingOptionsDirtyChange = useCallback(
    (isDirty: boolean) => handleDirtyChange('bookingOptions', isDirty),
    [handleDirtyChange]
  );
  const handleIndividualCardsDirtyChange = useCallback(
    (isDirty: boolean) => handleDirtyChange('individualCards', isDirty),
    [handleDirtyChange]
  );
  const handlePrePaidAllowancesDirtyChange = useCallback(
    (isDirty: boolean) => handleDirtyChange('prePaidAllowances', isDirty),
    [handleDirtyChange]
  );
  const handleSelectExtrasDirtyChange = useCallback(
    (isDirty: boolean) => handleDirtyChange('selectExtras', isDirty),
    [handleDirtyChange]
  );

  useEffect(() => {
    setDirtyBaselineAllowances(bookingAllowances);
  }, [bookingAllowances]);

  const handleBookingOptions = (data: Record<string, string>, switchState: SwitchState) => {
    const upsellItemsAllowed = mapSwitchState(switchState);
    const extrasCodes: string[] = [];

    Object.entries(switchState).forEach(([key, value]) => {
      if (value && extrasCodesMapping[key]) {
        extrasCodes.push(extrasCodesMapping[key]);
      }
    });
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        upsellItemsAllowed,
        extrasCodes,
      },
      submittedForms: { ...prev.submittedForms, bookingOptions: true },
    }));
  };
  const handleIndividualCards = (data: Record<string, number>) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        allowIndividualCards: data.individualPaymentCard,
      },
      submittedForms: { ...prev.submittedForms, individualCards: true },
    }));
  };

  const handlePrePaidAllowances = (data: {
    unitedKingdom: number;
    greaterLondon: number;
    germanyIreland: number;
    includeAlcohol: boolean;
  }) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        maxDinnerBudgets: {
          uKWide: {
            currency: Currency.GBP_NAME,
            amount: data.unitedKingdom,
          },
          greaterLondon: {
            currency: Currency.GBP_NAME,
            amount: data.greaterLondon,
          },
          ireland: {
            currency: Currency.EUR_NAME,
            amount: data.germanyIreland,
          },
        },
        allowAlcohol: data.includeAlcohol,
      },
      submittedForms: { ...prev.submittedForms, prePaidAllowances: true },
    }));
  };
  const handleSelectExtras = (data: FormData) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        allowCarParking: data.switchState.carParking,
        allowAdditionalCosts: data.switchState.additionalCosts,
      },
      submittedForms: { ...prev.submittedForms, selectExtras: true },
    }));
  };

  const resetForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        bookingOptions: false,
        individualCards: false,
        prePaidAllowances: false,
        selectExtras: false,
      },
    }));
  };

  const handleResponse = (
    updateResponse: { status: string },
    updatedAllowances: BookingAllowancesCriteria
  ) => {
    const scrollTo = () => {
      return window.scrollTo({ top: 0, behavior: 'smooth' });
    };
    if (updateResponse?.status === requestStatus.success) {
      toast({
        content: t('company.notification.message.save'),
      });
      setDirtyBaselineAllowances(updatedAllowances);
      setDirtySections({
        bookingOptions: false,
        individualCards: false,
        prePaidAllowances: false,
        selectExtras: false,
      });
    } else {
      toast({
        content: t('company.notification.message.error'),
        variant: 'error',
      });
    }
    resetForms();
    setIsUpdating(false);
    scrollTo();
    router.refresh();
  };

  useEffect(() => {
    if (
      !formState.submittedForms.bookingOptions ||
      !formState.submittedForms.individualCards ||
      !formState.submittedForms.prePaidAllowances ||
      !formState.submittedForms.selectExtras
    ) {
      return;
    }

    const saveBookingAllowances = async () => {
      setIsUpdating(true);
      const updateResponse = await updateBookingAllowances(
        companyId,
        formState.data,
        idTokenCookie
      );

      handleResponse(updateResponse, formState.data as BookingAllowancesCriteria);
    };

    if (!isUpdating) {
      saveBookingAllowances();
    }
  }, [formState]);

  const handleFormSubmit = async () => {
    bookingOptionsFormRef?.current?.requestSubmit();
    individualCardsFormRef?.current?.requestSubmit();
    prePaidAllowancesFormRef?.current?.requestSubmit();
    selectExtrasFormRef?.current?.requestSubmit();
  };

  return (
    <div className={`${containerStyle}`} data-testid={`${baseDataTestId}-container`}>
      <BookingOptionsForm
        onSubmit={handleBookingOptions}
        formRef={bookingOptionsFormRef}
        bookingAllowances={dirtyBaselineAllowances}
        onDirtyChange={handleBookingOptionsDirtyChange}
      />

      <IndividualPaymentCardsForm
        onSubmit={handleIndividualCards}
        formRef={individualCardsFormRef}
        bookingAllowances={dirtyBaselineAllowances}
        onDirtyChange={handleIndividualCardsDirtyChange}
      />

      <PrePaidAllowancesForm
        onSubmit={handlePrePaidAllowances}
        formRef={prePaidAllowancesFormRef}
        icons={icons}
        bookingAllowances={dirtyBaselineAllowances}
        onDirtyChange={handlePrePaidAllowancesDirtyChange}
      />

      <SelectExtrasForm
        onSubmit={handleSelectExtras}
        formRef={selectExtrasFormRef}
        bookingAllowances={dirtyBaselineAllowances}
        onDirtyChange={handleSelectExtrasDirtyChange}
      />

      <div className={buttonContainerStyle}>
        <Button
          data-testid={`${baseDataTestId}-save-changes-button`}
          variant="dialogDefault"
          className={buttonStyle}
          onClick={() => handleFormSubmit()}
          disabled={isUpdating}
        >
          {t('company.coMngt.allowances.saveUpdates')}
        </Button>
      </div>

      {!isUpdating && isFormDirty && <ReviewChanges />}
    </div>
  );
}

const buttonStyle = 'flex w-full';
const buttonContainerStyle = 'mt-12 mb-6 flex flex-col gap-4 mobile:w-full w-1/2';
const containerStyle = 'relative w-full flex flex-col mobile:w-full';
