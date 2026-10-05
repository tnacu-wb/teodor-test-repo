'use client';

import {
  Language,
  RegistrationQuestionWithAnswer,
  AddressInfo,
  CompanyDetailsCard,
  EmployeeCriteria,
  requestErrors,
  requestStatus,
  AccessLevel,
  SS_ALTERNATE_PATH,
  URLParams,
  EmployeeStatus,
  CompanyType,
} from '@whitbread-eos/api';
import {
  useToast,
  Button,
  FormPage,
  Notification,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import {
  GLOBALS,
  analytics,
  getAuthCookie,
  useTranslation,
  getPathForLocale,
  getLocaleByPathname,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils';
import {
  updateEmployeeDetails,
  addNewEmployee,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import Link from 'next/link';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useRef, useState, useEffect } from 'react';

import {
  Analytics,
  EmployeeAnalyticsRef,
  roleToAnalyticsCaption,
  PageNames,
} from '~components/innBusiness/ManageEmployeesAnalytics';
import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { CompanyAddress } from '~components/innBusiness/forms/CompanyAddressForm';
import { PaymentCardForm } from '~components/innBusiness/forms/PaymentCardForm';
import { PersonalDetailsForm } from '~components/innBusiness/forms/PersonalDetailsForm';
import { RegistrationQuestionsForm } from '~components/innBusiness/forms/RegistrationQuestionsForm';
import { UserRoleForm } from '~components/innBusiness/forms/UserRoleForm';

import { revalidateCacheOnLink } from '../../../cards/components/revalidate-link';

type formType = {
  data: EmployeeCriteria;
  submittedForms: Record<string, boolean>;
};

type Props = {
  id?: string;
  icons: Record<string, string>;
  language?: Language;
  registrationQuestions?: RegistrationQuestionWithAnswer[];
  companyAddress?: AddressInfo;
  employeeStatus?: string;
  userDetails?: {
    title: string;
    firstName: string;
    lastName: string;
    emailAddress: string;
    phoneNumber: string;
    mobileNumber: string;
    address: AddressInfo;
    centralCardId: string;
    employeeId: string;
    accessLevel: AccessLevel;
  };
  paymentCards: CompanyDetailsCard[];
  companyId: string;
  token?: string;
  isMainContact?: boolean;
  backUrl?: string;
  companyType?: string;
};

export function AddEditEmployee({
  id,
  icons,
  language = GLOBALS.language.EN as Language,
  registrationQuestions = [],
  companyAddress,
  paymentCards,
  employeeStatus,
  userDetails,
  isMainContact = false,
  companyId,
  token,
  backUrl = undefined,
  companyType,
}: Props) {
  const { toast } = useToast();
  const router = useRouter();
  const idTokenCookie = getAuthCookie();
  const baseDataTestId = 'AddEditEmployee';
  const isEdit = !!id;
  const initialAddress = isEdit ? userDetails?.address : companyAddress;
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const searchParams = useSearchParams();
  const isAlternateSource = !!searchParams?.get(URLParams.alternateSource);
  const { t } = useTranslation('users');
  const personalDetailsFormRef = useRef<HTMLFormElement | null>(null);
  const addressFormRef = useRef<HTMLFormElement | null>(null);
  const roleFormRef = useRef<HTMLFormElement | null>(null);
  const cardFormRef = useRef<HTMLFormElement | null>(null);
  const questionsFormRef = useRef<HTMLFormElement | null>(null);

  const analyticsRef = useRef<EmployeeAnalyticsRef | null>(null);

  const [isUpdating, setIsUpdating] = useState(false);
  const [showFailError, setShowFailError] = useState(false);
  const [showLastTMError, setShowLastTMError] = useState(false);
  const [isAddressOpen, setIsAddressOpen] = useState(false);
  const [isFormDirty, setIsFormDirty] = useState(false);
  const [formState, setFormState] = useState<formType>({
    data: {
      firstName: '',
      lastName: '',
      emailAddress: '',
      address: initialAddress,
      employeeAnswers: {},
    },
    submittedForms: {
      personalDetails: false,
      companyDetails: true, //becomes false when opening address editor
      userRoleDetails: false,
      paymentDetails: false,
      regQuestionsDetails: false,
    },
  });

  const pageName = isEdit ? 'Manage Employees: Edit Employee' : 'Manage Employees: Add An Employee';
  const { isBusinessPayManager } = getDetailsFromToken(token);

  const handlePersonalDetails = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: { ...prev.data, ...data },
      submittedForms: { ...prev.submittedForms, personalDetails: true },
    }));
  };

  const handleCompanyAddress = (data: AddressInfo) => {
    setFormState((prev) => ({
      data: { ...prev.data, address: { ...data } },
      submittedForms: { ...prev.submittedForms, companyDetails: true },
    }));
  };

  const handleUserRole = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: { ...prev.data, ...data },
      submittedForms: { ...prev.submittedForms, userRoleDetails: true },
    }));
  };

  const handlePaymentCard = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: { ...prev.data, ...data },
      submittedForms: { ...prev.submittedForms, paymentDetails: true },
    }));
  };

  const handleRegistrationQuestionsDetails = (data: any) => {
    setFormState((prev) => ({
      data: { ...prev.data, employeeAnswers: data },
      submittedForms: { ...prev.submittedForms, regQuestionsDetails: true },
    }));
  };

  const resetForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        personalDetails: false,
        companyDetails: !isAddressOpen,
        userRoleDetails: false,
        paymentDetails: false,
        regQuestionsDetails: false,
      },
    }));
  };

  useEffect(() => {
    analytics.update({
      innBusiness: {
        ...(window.analyticsData.innBusiness ?? {}),
        editEmployee: false,
        deleteEmployee: false,
      },
    });
  }, []);

  useEffect(() => {
    if (
      !formState.submittedForms.personalDetails ||
      !formState.submittedForms.companyDetails ||
      !formState.submittedForms.userRoleDetails ||
      (!isBusinessPayManager && !formState.submittedForms.paymentDetails) ||
      !formState.submittedForms.regQuestionsDetails
    ) {
      return;
    }
    const updateEmployee = async () => {
      setIsUpdating(true);
      const updateResponse = await updateEmployeeDetails(
        companyId,
        id ?? '',
        language?.toUpperCase(),
        formState.data,
        idTokenCookie
      );
      if (updateResponse?.status === requestStatus.success) {
        const isDeleted = formState.data.employeeStatus === EmployeeStatus.Purged;
        if (isDeleted) {
          analyticsRef.current?.setPageName(PageNames.MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_DELETION);
        }

        toast({
          content: t('userMgmt.employee.edit.success'),
        });
        await revalidateCacheOnLink(getPathForLocale(locale, `manage/employees/${id}`));
        await revalidateCacheOnLink(getPathForLocale(locale, `manage/employees`));
        router.push(getPathForLocale(locale, `manage/employees`));

        analytics.update({
          innBusiness: {
            ...(window.analyticsData.innBusiness ?? {}),
            editEmployee: true,
            deleteEmployee: isDeleted,
          },
        });
        window?._satellite?.track('saveUpdates');
      } else {
        if (updateResponse?.error === requestErrors.lastTravelManager) {
          setShowLastTMError(true);
        } else {
          window.scrollTo({ top: 0, behavior: 'smooth' });
          setShowFailError(true);
        }
        setIsUpdating(false);
        resetForms();
      }
    };

    const addEmployee = async () => {
      setIsUpdating(true);
      const addEmployeeResponse = await addNewEmployee(
        companyId,
        language?.toUpperCase(),
        formState.data,
        idTokenCookie
      );
      if (addEmployeeResponse?.status === requestStatus.success) {
        analyticsRef.current?.setAddEmployeeAnalyticsData({
          addEmployee: true,
          employeeRole: roleToAnalyticsCaption[formState.data.accessLevel as AccessLevel],
          employee: 'individual',
        });

        analyticsRef.current?.setPageName(PageNames.MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_CREATION);

        toast({
          content: t('userMgmt.employee.bulkUpload.success'),
        });

        await revalidateCacheOnLink(getPathForLocale(locale, `manage/employees`));

        const newEmployeeId = addEmployeeResponse.employeeId;
        const alternatePath = sessionStorage.getItem(SS_ALTERNATE_PATH);
        sessionStorage.removeItem(SS_ALTERNATE_PATH);
        router.push(
          isAlternateSource && alternatePath
            ? `${alternatePath}&${URLParams.employeeId}=${newEmployeeId}`
            : getPathForLocale(locale, `manage/employees`)
        );

        window?._satellite?.track('saveUpdates');
      } else {
        window.scrollTo({ top: 0, behavior: 'smooth' });
        setShowFailError(true);
        setIsUpdating(false);
        resetForms();
      }
    };

    if (!isUpdating) {
      isEdit ? updateEmployee() : addEmployee();
    }
  }, [formState]);

  const handleFormSubmit = async () => {
    personalDetailsFormRef?.current?.requestSubmit();
    addressFormRef?.current?.requestSubmit();
    roleFormRef?.current?.requestSubmit();
    cardFormRef?.current?.requestSubmit();
    questionsFormRef?.current?.requestSubmit();
    setShowLastTMError(false);
  };

  const handleFormDirtyChange = (isDirty: boolean) => {
    setIsFormDirty(isDirty);
  };

  const backHref = backUrl
    ? getPathForLocale(locale, decodeURIComponent(backUrl))
    : getPathForLocale(locale, `manage/employees`);

  return (
    <FormPage
      baseDataTestId={baseDataTestId}
      backIcon={formatIBAssetsUrl(icons?.['icon.arrow.left.purple'])}
      iconClassName="px-2 py-3 max-w-[3rem]"
      backHref={backHref}
      title={isEdit ? t('userMgmt.backToEmployeeDetails') : t('userMgmt.employee.add.heading')}
    >
      {showFailError && (
        <Notification
          className={notificationStyle}
          type="error"
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          title={t('userMgmt.employee.edit.error.failure.heading')}
          message={t('userMgmt.employee.edit.error.failure.description')}
        />
      )}
      {!isEdit && (
        <>
          <p className={paragraphStyle}>{t('userMgmt.employee.add.description')}</p>
          {companyType !== CompanyType.BUSINESS_PAY && (
            <Link className={linkStyle} href="add-bulk">
              {t('userMgmt.employee.add.multiple.label')}
            </Link>
          )}
          <Link className={linkStyle} href="invite">
            {t('userMgmt.employee.add.invite.label')}
          </Link>
        </>
      )}

      <PersonalDetailsForm
        formRef={personalDetailsFormRef}
        icons={icons}
        onSubmit={handlePersonalDetails}
        language={language}
        userDetails={isEdit ? userDetails : undefined}
        onDirtyChange={handleFormDirtyChange}
      />
      <CompanyAddress
        formRef={addressFormRef}
        icons={icons}
        onSubmit={handleCompanyAddress}
        language={language}
        addressData={initialAddress}
        onOpen={() => {
          setIsAddressOpen(true);
          setFormState((prev) => ({
            data: { ...prev.data },
            submittedForms: { ...prev.submittedForms, companyDetails: false },
          }));
        }}
        onDirtyChange={handleFormDirtyChange}
      />
      <UserRoleForm
        formRef={roleFormRef}
        onSubmit={handleUserRole}
        infoTooltipIcon={formatIBAssetsUrl(icons?.['icon.notification.info'])}
        arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
        errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
        notificationInfo={formatIBAssetsUrl(icons?.['icon.notification.info'])}
        employeeStatus={employeeStatus}
        userDetails={userDetails}
        isMainContact={isMainContact}
        isEditEmployeePage={isEdit}
        language={language}
        token={token}
        companyId={companyId}
        setShowLastTMError={(value: boolean) => setShowLastTMError(value)}
        showLastTMError={showLastTMError}
        onDirtyChange={handleFormDirtyChange}
      />
      {!isBusinessPayManager && (
        <PaymentCardForm
          cards={paymentCards}
          onSubmit={handlePaymentCard}
          icons={icons}
          locale={locale}
          selectedCardId={isEdit ? userDetails?.centralCardId : undefined}
          formRef={cardFormRef}
          onDirtyChange={handleFormDirtyChange}
        />
      )}

      <RegistrationQuestionsForm
        formRef={questionsFormRef}
        questions={registrationQuestions}
        onSubmit={handleRegistrationQuestionsDetails}
        icons={icons}
        onDirtyChange={handleFormDirtyChange}
      />

      <div className={buttonContainerStyle}>
        <Button
          data-testid="Submit-Employee-Details"
          variant="dialogDefault"
          className={buttonStyle}
          onClick={() => handleFormSubmit()}
          disabled={isUpdating}
        >
          {isEdit ? t('userMgmt.button.saveUpdates') : t('userMgmt.employee.add.form.submit')}
        </Button>
        <Link href={getPathForLocale(locale, `manage/employees`)}>
          <Button
            data-testid="IB-Add-Edit-Cancel-Button"
            variant="dialogOutline"
            className={buttonStyle}
          >
            {t('userMgmt.employee.add.form.cancel')}
          </Button>
        </Link>
      </div>

      <Notification
        className={notificationStyle}
        type="info"
        icon={formatIBAssetsUrl(icons?.['icon.notification.info'])}
        title={t('userMgmt.employee.add.infobox.heading')}
        message={
          <SanitizedContent>
            {t('userMgmt.employee.bulkUpload.infobox.description')}
          </SanitizedContent>
        }
      />

      {!isUpdating && isFormDirty && <ReviewChanges />}
      <Analytics ref={analyticsRef} pageName={pageName} />
    </FormPage>
  );
}

const paragraphStyle = 'font-normal text-base mt-4';
const linkStyle = 'flex font-medium text-sm underline text-secondaryColor mt-4';
const buttonStyle = 'flex w-full';
const buttonContainerStyle = 'mt-12 flex flex-col gap-3';
const notificationStyle = 'mt-12';
