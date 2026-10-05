'use client';

import {
  AddressInfo,
  LOCALES,
  requestStatus,
  Customer,
  AddressType,
  EmployeeCriteria,
} from '@whitbread-eos/api';
import {
  Button,
  Notification,
  SanitizedContent,
  useToast,
  ButtonVariantDescriptor,
} from '@whitbread-eos/atoms/ui';
import { getAuthCookie } from '@whitbread-eos/utils';
import {
  formatIBAssetsUrl,
  getCountryLanguageByLocale,
  updateProfileDetails,
  useTranslation,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import React, { useEffect, useRef, useState } from 'react';

import { AddressDetails } from '~components/innBusiness/AddressDetails';
import { UserDetails } from '~components/innBusiness/UserDetails';
import { CompanyAddress } from '~components/innBusiness/forms/CompanyAddressForm';
import { TypeOfAddress } from '~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress';
import { CompanyName } from '~components/innBusiness/forms/CompanyDetailsForm/CompanyName';
import { PersonalDetailsForm } from '~components/innBusiness/forms/PersonalDetailsForm';

import { buildProfileFieldErrors, ProfileFieldErrors } from '../../utils/profile-errors';
import { TrackableComponent } from '../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';

type Props = {
  baseDataTestId?: string;
  icons: Record<string, string>;
  profileDetails: Customer;
  locale?: LOCALES;
} & TrackableComponent;

type formType = {
  data: Record<string, any>;
  submittedForms: Record<string, boolean>;
};

export function YourProfile({
  baseDataTestId,
  profileDetails: initialProfileDetails,
  locale,
  icons,
  onReviewChangesToggle,
  onIsEditableToggle,
  onIsUpdatingToggle,
  onIsDirtyToggle,
  isEditable,
  isUpdating,
  isDirty,
}: Props) {
  const { t } = useTranslation(['profile', 'users']);
  const { language } = getCountryLanguageByLocale(locale);
  const idTokenCookie = getAuthCookie();
  const { toast } = useToast();
  const router = useRouter();

  const [profileDetails, setProfileDetails] = useState<Customer>(initialProfileDetails);
  const [profileFieldErrors, setProfileFieldErrors] = useState<ProfileFieldErrors>({});

  const [showEditButton, setShowEditButton] = useState<boolean>(true);
  const [isAddressOpen, setIsAddressOpen] = useState(false);
  const [addressType, setAddressType] = useState(
    profileDetails?.contactDetail?.address?.companyName ? AddressType.Business : AddressType.Home
  );

  const isBusinessAddress = addressType === AddressType.Business;
  const shouldSubmitRef = useRef(false);

  const [formState, setFormState] = useState<formType>({
    data: {
      contactDetail: profileDetails?.contactDetail,
      paymentPreference: profileDetails?.paymentPreference,
      bookingPreference: profileDetails?.bookingPreference,
    },
    submittedForms: {
      personalDetails: false,
      addressDetails: true, //becomes false when opening address editor
      companyName: false,
      addressType: false,
    },
  });

  const formattedAddress: AddressInfo = {
    addressLine1: profileDetails?.contactDetail?.address?.line1 ?? '',
    addressLine2: profileDetails?.contactDetail?.address?.line2,
    addressLine3: profileDetails?.contactDetail?.address?.line3,
    addressLine4: profileDetails?.contactDetail?.address?.line4,
    addressLine5: profileDetails?.contactDetail?.address?.line5,
    country: profileDetails?.contactDetail?.address?.countryCode ?? '',
    postCode: profileDetails?.contactDetail?.address?.postCode ?? '',
  };
  const formattedUserInfos: EmployeeCriteria = {
    title: profileDetails?.contactDetail?.title ?? '',
    firstName: profileDetails?.contactDetail?.firstName ?? '',
    lastName: profileDetails?.contactDetail?.lastName ?? '',
    emailAddress: profileDetails?.contactDetail?.email ?? '',
    phoneNumber: profileDetails?.contactDetail?.telephone ?? '',
    mobileNumber: profileDetails?.contactDetail?.mobile ?? '',
  };

  const personalDetailsFormRef = useRef<HTMLFormElement | null>(null);
  const addressFormRef = useRef<HTMLFormElement | null>(null);
  const companyNameFormRef = useRef<HTMLFormElement | null>(null);
  const addressTypeFormRef = useRef<HTMLFormElement | null>(null);

  useEffect(() => {
    if (isEditable === false) {
      setShowEditButton(true);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [isEditable]);

  const handleButtonClick = () => {
    setFormState({
      data: {
        contactDetail: profileDetails?.contactDetail,
        paymentPreference: profileDetails?.paymentPreference,
        bookingPreference: profileDetails?.bookingPreference,
      },
      submittedForms: {
        personalDetails: false,
        addressDetails: true,
        companyName: false,
        addressType: false,
      },
    });

    setShowEditButton(false);
    onIsEditableToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
  };

  const handlePersonalDetails = (data: Record<string, string>) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          title: data.title,
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.emailAddress,
          telephone: data.phoneNumber,
          mobile: data.mobileNumber,
        },
      },
      submittedForms: { ...prev.submittedForms, personalDetails: true },
    }));
  };
  const handleCompanyAddress = (data: AddressInfo) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            line1: data.addressLine1,
            line2: data.addressLine2,
            line3: data.addressLine3,
            line4: data.addressLine4,
            line5: data.addressLine5,
            postCode: data.postCode,
            countryCode: data.country,
          },
        },
      },
      submittedForms: { ...prev.submittedForms, addressDetails: true },
    }));
  };
  const handleCompanyName = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            companyName: isBusinessAddress ? data.companyName : null,
          },
        },
      },
      submittedForms: { ...prev.submittedForms, companyName: true },
    }));
  };

  const handleAddressType = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            type: data.addressType,
            companyName: isBusinessAddress
              ? profileDetails?.contactDetail?.address?.companyName
              : null,
          },
        },
      },
      submittedForms: { ...prev.submittedForms, addressType: true },
    }));
  };

  const handleAddressTypeChange = (type: AddressType) => {
    setAddressType(type);

    if (type !== addressType) {
      onIsDirtyToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    }
  };

  const handleFormSubmit = async () => {
    personalDetailsFormRef?.current?.requestSubmit();
    addressFormRef?.current?.requestSubmit();
    addressTypeFormRef?.current?.requestSubmit();
    if (isBusinessAddress) {
      companyNameFormRef?.current?.requestSubmit();
    }

    shouldSubmitRef.current = true;
  };

  const resetForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        personalDetails: false,
        addressDetails: !isAddressOpen,
        companyName: false,
        addressType: false,
      },
    }));
  };

  useEffect(() => {
    const allFormsSubmitted =
      formState.submittedForms.personalDetails &&
      formState.submittedForms.addressDetails &&
      (isBusinessAddress ? formState.submittedForms.companyName : true) &&
      formState.submittedForms.addressType;

    if (!allFormsSubmitted || !shouldSubmitRef.current || isUpdating) {
      return;
    }

    const updateProfile = async () => {
      onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
      shouldSubmitRef.current = false;
      setProfileFieldErrors({});

      try {
        const updateResponse = await updateProfileDetails(
          profileDetails?.contactDetail?.email,
          formState.data,
          idTokenCookie
        );

        if (updateResponse?.status === requestStatus.success) {
          setProfileDetails((prev: Customer) => ({
            ...prev,
            contactDetail: {
              ...formState.data.contactDetail,
            },
            paymentPreference: formState.data.paymentPreference,
            bookingPreference: formState.data.bookingPreference,
          }));

          toast({
            content: t('profile.profile.notification.success'),
          });
          onIsEditableToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
          onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
          onIsDirtyToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
          window.scrollTo({ top: 0, behavior: 'smooth' });
          router.refresh();
        } else {
          const fieldErrors = buildProfileFieldErrors(
            updateResponse?.profileErrorCodes ?? [],
            t,
            formState.data?.contactDetail
          );
          if (Object.keys(fieldErrors).length > 0) {
            setProfileFieldErrors(fieldErrors);
            onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
            resetForms();
            return;
          }

          toast({
            content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
            variant: 'error',
          });
          onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
          resetForms();
        }
      } catch (error) {
        toast({
          content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
          variant: 'error',
        });
        onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
        resetForms();
      }
    };

    updateProfile();
  }, [formState]);

  const handleIsDirty = (newValue: boolean) => {
    onIsDirtyToggle(CHANGES_TRACKER.YOUR_PROFILE, newValue);
  };

  const handleCancelEdit = () => {
    if (isDirty) {
      onReviewChangesToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    } else {
      onIsEditableToggle(CHANGES_TRACKER.YOUR_PROFILE, false);
    }
  };

  return (
    <div data-testid={`${baseDataTestId}-Your-Profile-Container`} className={containerStyle}>
      <h4 data-testid={`${baseDataTestId}-Your-Profile-Title`} className={titleStyle}>
        {isEditable ? t('profile.profile.editProfile') : t('profile.profile.title')}
      </h4>
      {isEditable ? (
        <div className={formWrappers}>
          <Notification
            className={notificationStyle}
            type="info"
            icon={formatIBAssetsUrl(icons['icon.notification.info'])}
            message={
              <SanitizedContent>{t('profile.profile.editProfile.notification')}</SanitizedContent>
            }
          />
          <PersonalDetailsForm
            formRef={personalDetailsFormRef}
            icons={icons}
            onSubmit={handlePersonalDetails}
            language={language}
            userDetails={formattedUserInfos}
            onDirtyChange={handleIsDirty}
            serverErrors={profileFieldErrors}
          />
          <h4 data-testid={`${baseDataTestId}-profile-your-address-title`} className={headingStyle}>
            {t('profile.profile.yourAddress')}
          </h4>
          <TypeOfAddress
            formRef={addressTypeFormRef}
            onSubmit={handleAddressType}
            icons={icons}
            companyName={profileDetails?.contactDetail?.address?.companyName ?? ''}
            onAddressTypeChange={handleAddressTypeChange}
          />
          {isBusinessAddress && (
            <CompanyName
              formRef={companyNameFormRef}
              onSubmit={handleCompanyName}
              icons={icons}
              companyName={profileDetails?.contactDetail?.address?.companyName ?? ''}
              isAddressType
            />
          )}
          <CompanyAddress
            formRef={addressFormRef}
            icons={icons}
            onSubmit={handleCompanyAddress}
            language={language}
            addressData={formattedAddress}
            onOpen={() => {
              setIsAddressOpen(true);
              setFormState((prev) => ({
                data: { ...prev.data },
                submittedForms: { ...prev.submittedForms, addressDetails: false },
              }));
            }}
            postalCode={profileDetails?.contactDetail?.address.postCode}
            isCompanyDetailsContainer
            isProfilePage
            onDirtyChange={handleIsDirty}
          />
        </div>
      ) : (
        <div className={yourProfileWrapperStyle}>
          <UserDetails
            userInformation={formattedUserInfos}
            companyName={profileDetails?.contactDetail?.address?.companyName ?? ''}
            displayCompanyName
          />
          <AddressDetails language={language} addressInfo={formattedAddress} />
        </div>
      )}
      {showEditButton && (
        <div>
          <Button
            variant="downloadButton"
            size="downloadButton"
            className={buttonStyle}
            onClick={handleButtonClick}
            data-testid={`${baseDataTestId}-Your-Profile-Edit-Profile-Button`}
          >
            {t('profile.profile.button.edit')}
          </Button>
        </div>
      )}

      {isEditable && (
        <div className={buttonContainerStyle}>
          <Button
            data-testid={`${baseDataTestId}-Your-Profile-save-changes`}
            variant="saveUpdatesButton"
            className={`${secondaryButtonStyle}`}
            onClick={() => handleFormSubmit()}
            disabled={isUpdating}
          >
            {t('profile.profile.form.button.save')}
          </Button>
          <Button
            variant="editButton"
            size="newAddressButton"
            className={`${secondaryButtonStyle} justify-start`}
            onClick={handleCancelEdit}
            data-testid={`${baseDataTestId}-Your-Profile-DiscardButton`}
          >
            {t('profile.profile.form.button.cancel')}
          </Button>
        </div>
      )}
    </div>
  );
}

const containerStyle = 'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3';
const titleStyle = 'font-bold text-[1.438rem]';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] h-[3.5rem] mt-[1.5rem] mb-[4rem] border border-secondaryColor ml-auto bg-white ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const yourProfileWrapperStyle = 'space-y-4 mt-4';
const secondaryButtonStyle = 'flex w-1/2 mobile:w-full';
const buttonContainerStyle =
  'pb-16 w-1/2 gap-3 justify-between mobile:flex-col mobile:w-full mobile:items-center';
const formWrappers = 'w-1/2 pb-10 mobile:w-full';
const headingStyle = 'font-bold text-xl mt-10 mb-6';
const notificationStyle = 'mt-6 w-full';
