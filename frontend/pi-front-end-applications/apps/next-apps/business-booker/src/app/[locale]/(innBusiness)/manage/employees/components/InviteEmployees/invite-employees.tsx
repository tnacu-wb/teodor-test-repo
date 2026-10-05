'use client';

import { CompanyDetailsCard } from '@whitbread-eos/api';
import {
  FormPage,
  Button,
  Notification,
  SanitizedContent,
  useToast,
} from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  getPathForLocale,
  formatIBAssetsUrl,
  getLocaleByPathname,
  useTranslation,
} from '@whitbread-eos/utils';
import { sendActivationEmail } from '@whitbread-eos/utils/server';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { CompanyEmailForm } from '~components/innBusiness/forms/CompanyEmailForm';
import { PaymentCardForm } from '~components/innBusiness/forms/PaymentCardForm';

type Props = {
  companyId: string;
  paymentCards: CompanyDetailsCard[];
  icons: Record<string, string>;
  language: string;
  isBusinessPayManager?: boolean;
};

type FormType = {
  data: {
    email: string;
    cardId: string;
  };
  submittedForms: Record<string, boolean>;
};

export function InviteEmployees({
  companyId,
  paymentCards,
  icons,
  language,
  isBusinessPayManager,
}: Props) {
  const baseDataTestId = 'InviteEmployees';
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation('users');
  const emailFormRef = useRef<HTMLFormElement | null>(null);
  const cardFormRef = useRef<HTMLFormElement | null>(null);
  const [isUpdating, setIsUpdating] = useState(false);
  const { toast } = useToast();
  const router = useRouter();
  const [formState, setFormState] = useState<FormType>({
    data: {
      email: '',
      cardId: '',
    },
    submittedForms: {
      companyEmail: false,
      paymentCard: false,
    },
  });

  useEffect(() => {
    if (!formState.submittedForms.companyEmail || !formState.submittedForms.paymentCard) {
      return;
    }

    const inviteEmployee = async () => {
      setIsUpdating(true);

      const res = await sendActivationEmail(
        getAuthCookie(),
        companyId,
        language,
        formState.data.email,
        formState.data.cardId
      );

      if (res || res === '') {
        router.push(getPathForLocale(locale, 'manage/employees'));
        toast({
          content: t('userMgmt.employee.bulkUpload.success'),
        });

        return;
      }

      toast({
        content: t('userMgmt.manageEmployees.resendCode.notification.failure'),
        variant: 'error',
      });

      setIsUpdating(false);
      setFormState((prev) => ({
        ...prev,
        submittedForms: {
          companyEmail: false,
          paymentCard: false,
        },
      }));
    };

    inviteEmployee();
  }, [formState]);

  const handleCompanyEmail = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: { ...prev.data, ...data },
      submittedForms: { ...prev.submittedForms, companyEmail: true },
    }));
  };

  const handlePaymentCard = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: { ...prev.data, cardId: data.centralCardId },
      submittedForms: { ...prev.submittedForms, paymentCard: true },
    }));
  };

  const handleSubmitClick = () => {
    emailFormRef?.current?.requestSubmit();
    if (!isBusinessPayManager) {
      cardFormRef?.current?.requestSubmit();
    } else {
      // Only set cardId and mark as submitted if the payment form is not rendered
      setFormState((prev) => ({
        ...prev,
        data: { ...prev.data, cardId: '1' },
        submittedForms: { ...prev.submittedForms, paymentCard: true },
      }));
    }
  };

  return (
    <FormPage
      iconClassName="px-2 py-3 max-w-[3rem]"
      baseDataTestId={baseDataTestId}
      backIcon={formatIBAssetsUrl(icons['icon.arrow.left.purple'])}
      backHref={getPathForLocale(locale, `manage/employees/add`)}
      title={t('userMgmt.employee.add.invite.label')}
    >
      <CompanyEmailForm
        className={companyEmailStyle}
        onSubmit={handleCompanyEmail}
        icons={icons}
        formRef={emailFormRef}
      />
      {!isBusinessPayManager && (
        <PaymentCardForm
          cards={paymentCards}
          onSubmit={handlePaymentCard}
          icons={icons}
          locale={locale}
          formRef={cardFormRef}
          paymentTypeOnly
        />
      )}
      <Button
        data-testid={`${baseDataTestId}-Send-Invite-Button`}
        variant="dialogDefault"
        className={buttonStyle}
        onClick={handleSubmitClick}
        disabled={isUpdating}
      >
        {t('userMgmt.employee.invite.submit')}
      </Button>
      <Notification
        className={notificationStyle}
        type="info"
        icon={formatIBAssetsUrl(icons['icon.notification.info'])}
        title={t('userMgmt.employee.add.infobox.heading')}
        message={
          <SanitizedContent>
            {t('userMgmt.employee.bulkUpload.infobox.description')}
          </SanitizedContent>
        }
      />
    </FormPage>
  );
}

const companyEmailStyle = 'mt-12 mobile:mt-12';
const notificationStyle = 'mt-12';
const buttonStyle = 'mt-12 flex w-full';
