'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES, requestStatus, CustomerAccountDetails } from '@whitbread-eos/api';
import { Button, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie } from '@whitbread-eos/utils';
import {
  useTranslation,
  resetMemorableWord,
  getPathForLocale,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

import {
  getMemorableWordSchema,
  MemorableWordInput,
} from '~components/innBusiness/forms/MemorableWordInput/MemorableWordInput';

type Props = {
  baseDataTestId: string;
  icons: Record<string, string>;
  locale?: LOCALES;
  account: CustomerAccountDetails;
};

export function MemorableWordForm({ baseDataTestId, icons, locale, account }: Props) {
  const { t } = useTranslation(['users', 'spending']);
  const idTokenCookie = getAuthCookie();
  const { toast } = useToast();
  const router = useRouter();

  const [isUpdating, setIsUpdating] = useState(false);

  const memorableWordSchema = getMemorableWordSchema(
    t('spending.spending.memorable.word.validation')
  );

  const schema = z.object({
    memorableWord: memorableWordSchema,
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    clearErrors,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      memorableWord: '',
    },
  });

  const updateMemorableWord = async (data: Record<string, string>) => {
    setIsUpdating(true);

    try {
      const updateResponse = await resetMemorableWord(
        account.tetheredGuid,
        data?.memorableWord,
        idTokenCookie,
        account.scheme
      );
      setIsUpdating(false);

      if (updateResponse?.status === requestStatus.success) {
        setIsUpdating(false);
        router.push(
          getPathForLocale(locale, `spending?tab=innbusiness-pay&account=${account.tetheredGuid}`)
        );
        toast({
          content: t('spending.spending.memorable.word.notification'),
          icon: formatIBAssetsUrl(icons?.['icon.notification.success'] ?? ''),
        });
      } else {
        toast({
          content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
          variant: 'error',
        });
      }
    } catch {
      toast({
        content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
        variant: 'error',
      });
    }
  };

  return (
    <form
      onSubmit={handleSubmit(updateMemorableWord)}
      data-testid={`${baseDataTestId}-form`}
      className="flex flex-col gap-6 max-w-[26.25rem] mobile:max-w-[100%]"
    >
      <MemorableWordInput
        name="memorableWord"
        icons={icons}
        control={control}
        errors={errors}
        trigger={trigger}
        clearErrors={clearErrors}
      />
      <Button
        data-testid={`${baseDataTestId}-Submit-Button`}
        variant="saveUpdatesButton"
        disabled={isUpdating}
        type="submit"
      >
        {t('spending.spending.memorable.word.submit')}
      </Button>
    </form>
  );
}
