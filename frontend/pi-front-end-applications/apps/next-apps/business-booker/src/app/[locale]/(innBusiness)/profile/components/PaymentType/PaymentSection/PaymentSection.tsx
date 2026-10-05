'use client';

import { CountryDetails, Language, LOCALES, requestStatus } from '@whitbread-eos/api';
import { useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie } from '@whitbread-eos/utils';
import {
  getCountriesList,
  useTranslation,
  updateProfileDetails,
  formatIBAssetsUrl,
  getPathForLocale,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useState, useEffect } from 'react';

import { revalidateCacheOnLink } from '../../../../manage/cards/components/revalidate-link';
import { TrackableComponent } from '../../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../../ReviewChangesWrapper/ReviewChangesWrapper';
import PaymentCard from '../PaymentCard/PaymentCard';
import PaymentTypeCta from '../PaymentTypeCta/PaymentTypeCta';
import PaymentTypeSelection from '../PaymentTypeSelection/PaymentTypeSelection';

type ProfileDetails = {
  contactDetail?: {
    title?: string;
    firstName?: string;
    lastName?: string;
    email?: string;
    phoneNumber?: string;
    mobileNumber?: string;
    address?: {
      line1?: string;
      line2?: string;
      line3?: string;
      line4?: string;
      line5?: string;
      postCode?: string;
      countryCode?: string;
      companyName?: string | null;
      type?: string;
    };
  };
  bookingPreference?: Record<string, unknown>;
  paymentPreference?: {
    paymentCard?: {
      cardNumber?: string;
      expiryDate?: string;
      cardHolderName?: string;
      cardType?: string;
      cardToken?: string;
    };
  };
};

type PaymentSectionProps = {
  hasPaymentCard: boolean;
  icons: Record<string, string>;
  profileDetails?: ProfileDetails;
  token?: string;
  employeeId?: string;
  language?: Language;
  locale?: LOCALES;
} & TrackableComponent;

export default function PaymentSection({
  hasPaymentCard: initialHasPaymentCard,
  icons,
  profileDetails: initialProfileDetails,
  token,
  employeeId,
  language,
  locale,
  onIsEditableToggle,
  onIsDirtyToggle,
  isEditable,
  isDirty,
  ...trackableChangesProps
}: PaymentSectionProps) {
  const router = useRouter();
  const [showPaymentTypeSelection, setShowPaymentTypeSelection] = useState(false);
  const [hasPaymentCard, setHasPaymentCard] = useState(initialHasPaymentCard);
  const [profileDetails, setProfileDetails] = useState(initialProfileDetails);
  const [userAddress, setUserAddress] = useState('');
  const [paymentStatus, setPaymentStatus] = useState<'success' | 'error' | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const { t } = useTranslation('profile');
  const { toast } = useToast();
  const profilePath = getPathForLocale(locale?.toLowerCase() as LOCALES, 'profile');

  useEffect(() => {
    setHasPaymentCard(initialHasPaymentCard);
    setProfileDetails(initialProfileDetails);
  }, [initialHasPaymentCard, initialProfileDetails]);

  useEffect(() => {
    if (!isEditable) {
      setShowPaymentTypeSelection(false);
    }
  }, [isEditable]);

  useEffect(() => {
    const fetchCountries = async () => {
      if (showPaymentTypeSelection && profileDetails?.contactDetail?.address) {
        const countries = await getCountriesList(language);
        const countryCode = profileDetails.contactDetail.address.countryCode;
        const transformedCountryCode = countryCode === 'D' ? 'DE' : countryCode;
        const country = countries?.find(
          (c: CountryDetails) => c.countryCode === transformedCountryCode
        );

        const address = `${profileDetails.contactDetail.address.line1}${
          profileDetails.contactDetail.address.line2
            ? `, ${profileDetails.contactDetail.address.line2}`
            : ''
        }${
          profileDetails.contactDetail.address.line3
            ? `, ${profileDetails.contactDetail.address.line3}`
            : ''
        }${
          profileDetails.contactDetail.address.line4
            ? `, ${profileDetails.contactDetail.address.line4}`
            : ''
        }${
          profileDetails.contactDetail.address.postCode
            ? `, ${profileDetails.contactDetail.address.postCode}`
            : ''
        }${country?.countryName ? `, ${country.countryName}` : ''}`;

        setUserAddress(address);
      }
    };

    fetchCountries();
  }, [showPaymentTypeSelection, profileDetails, language]);

  const handleAddNew = () => {
    setPaymentStatus(null);
    setErrorMessage(null);
    setShowPaymentTypeSelection(true);
    onIsEditableToggle(CHANGES_TRACKER.PAYMENT_TYPE, true);
  };

  const handleDeleteCard = async () => {
    try {
      const idTokenCookie = getAuthCookie();

      const updatedData = {
        contactDetail: profileDetails?.contactDetail,
        bookingPreference: profileDetails?.bookingPreference,
        paymentPreference: {
          ...profileDetails?.paymentPreference,
          paymentCard: null,
        },
      };

      const updateResponse = await updateProfileDetails(
        profileDetails?.contactDetail?.email,
        updatedData,
        idTokenCookie
      );

      if (updateResponse?.status === requestStatus.success) {
        setHasPaymentCard(false);
        setProfileDetails({
          ...profileDetails,
          paymentPreference: {
            ...profileDetails?.paymentPreference,
            paymentCard: undefined,
          },
        });

        toast({
          content: t('profile.notification.success'),
          icon: formatIBAssetsUrl(icons['icon.notification.success']),
        });

        router.refresh();
        return Promise.resolve();
      } else {
        throw new Error('Failed to delete payment card');
      }
    } catch (error) {
      console.error('Error deleting payment card:', error);
      toast({
        content: t('generic.error'),
        variant: 'error',
      });
      return Promise.reject(error);
    }
  };

  const handleSubmit = async (formData: any) => {
    try {
      if (formData?.cardDetails) {
        setPaymentStatus('success');

        if (formData.paymentPreference?.paymentCard) {
          setHasPaymentCard(true);
          setProfileDetails({
            ...profileDetails,
            paymentPreference: {
              ...profileDetails?.paymentPreference,
              paymentCard: formData.paymentPreference.paymentCard,
            },
          });
        }

        setShowPaymentTypeSelection(false);
        onIsEditableToggle(CHANGES_TRACKER.PAYMENT_TYPE, false);

        await revalidateCacheOnLink(profilePath);
        router.refresh();

        return;
      }
    } catch (error) {
      setPaymentStatus('error');
      setErrorMessage('Failed to process payment. Please try again.');
      setShowPaymentTypeSelection(false);
      onIsEditableToggle(CHANGES_TRACKER.PAYMENT_TYPE, false);
    }
  };

  const handlePaymentError = (message: string) => {
    setPaymentStatus('error');
    setErrorMessage(message);
    setShowPaymentTypeSelection(false);
    onIsEditableToggle(CHANGES_TRACKER.PAYMENT_TYPE, false);
  };

  const handleCancel = () => {
    setShowPaymentTypeSelection(false);
    onIsEditableToggle(CHANGES_TRACKER.PAYMENT_TYPE, false);
  };

  if (showPaymentTypeSelection) {
    return (
      <PaymentTypeSelection
        icons={icons}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        onError={handlePaymentError}
        userAddress={userAddress}
        profileDetails={profileDetails}
        token={token}
        employeeId={employeeId}
        language={language}
        onIsEditableToggle={onIsEditableToggle}
        isEditable={isEditable}
        isDirty={isDirty}
        onIsDirtyToggle={onIsDirtyToggle}
        {...trackableChangesProps}
      />
    );
  }

  return hasPaymentCard ? (
    <PaymentCard
      icons={icons}
      profileDetails={profileDetails}
      onAddNew={() => {
        setShowPaymentTypeSelection(true);
        onIsEditableToggle(CHANGES_TRACKER.PAYMENT_TYPE, true);
        setPaymentStatus(null);
        setErrorMessage(null);
      }}
      onDelete={handleDeleteCard}
      paymentStatus={paymentStatus || undefined}
    />
  ) : (
    <PaymentTypeCta onClick={handleAddNew} errorMessage={errorMessage || undefined} icons={icons} />
  );
}
