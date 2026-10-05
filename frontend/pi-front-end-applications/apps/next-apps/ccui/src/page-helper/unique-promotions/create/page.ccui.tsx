import { Container, Text, Box, Flex, useToast } from '@chakra-ui/react';
import { type Claims, CREATE_PROMO_BATCH } from '@whitbread-eos/api';
import { FormProps } from '@whitbread-eos/atoms';
import { CreatePromoCodeFormContext } from '@whitbread-eos/molecules/dist/unique-promotions/Create';
import { useCustomLocale, useMutationRequest, usePromoTranslation } from '@whitbread-eos/utils';
import { ChevronLeft } from 'lucide-react';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import React, { SetStateAction, useCallback, useState, useEffect, useRef, useMemo } from 'react';

import {
  cancelReturnStyles,
  getPromoErrorMessage,
  onSubmitCreatePromotionForm,
  PromoFormDetails,
  regFormInit,
} from './common';
import { createPromotionFormConfig } from './formConfig/createPromotionFormConfig';

const ACTIVE_STATUSES = new Set(['COMPLETED', 'PENDING', 'RUNNING']);

const Form = dynamic(
  async () => {
    const { Form } = await import('@whitbread-eos/atoms');
    return { default: Form };
  },
  {
    ssr: false,
  }
);

interface CreatePromotionCodeProps {
  user?: Claims;
}

const IGNORED_ERROR_CODES = new Set([1010, 1011, 1012]);

export default function CreatePromotionCodeCcui({ user }: Readonly<CreatePromotionCodeProps>) {
  const t = usePromoTranslation();
  const router = useRouter();
  const toast = useToast();
  const hasMutationCompletedRef = useRef(false);
  const baseDataTestId = 'CreatePromotionCodePage';

  const { country, language: currentLang } = useCustomLocale();

  const [loadingTransition, setLoadingTransition] = useState(false);
  const [formDetails, setFormDetails] = useState<FormProps['defaultValues']>({
    ...regFormInit,
    requestedBy: user?.email ?? '',
  });

  const {
    mutation: createPromoMutation,
    data: createPromoData,
    isLoading: createPromoIsLoading,
    isError: createPromoIsError,
    error: createPromoError,
    isSuccess: createPromoIsSuccess,
  } = useMutationRequest(CREATE_PROMO_BATCH, true);

  const batchCompleted = useMemo(() => {
    return ACTIVE_STATUSES.has(createPromoData?.createPromoBatch?.status ?? '');
  }, [createPromoData]);

  const toastOptions = useMemo(
    () => ({
      duration: 6000,
      isClosable: true,
      position: 'top' as const,
    }),
    []
  );

  const goBackToBatchesList = useCallback(() => {
    router.replace(`/${country}/${currentLang}/unique-promotions/list`);
  }, [router, country, currentLang]);

  const handleSubmitCreatePromotionForm = useCallback(
    (data: PromoFormDetails, event: any): void => {
      void onSubmitCreatePromotionForm({
        data,
        event,
        userEmail: user?.email,
        createPromoMutation,
      });
    },
    [user?.email, createPromoMutation]
  );

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setFormDetails(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setFormDetails]
  );

  useEffect(() => {
    if (createPromoIsLoading) return;

    if (!hasMutationCompletedRef.current) {
      hasMutationCompletedRef.current = true;
      return;
    }

    const errorCode = Number(getPromoErrorMessage(createPromoError));

    if (createPromoIsSuccess && batchCompleted) {
      sessionStorage.setItem('promoBatchCreated', 'true');
      goBackToBatchesList();
    } else if (createPromoIsError && !IGNORED_ERROR_CODES.has(errorCode)) {
      toast({
        title: t.statusFailed,
        description: t.batchErrorMessage,
        status: 'warning',
        ...toastOptions,
      });
    }
  }, [
    goBackToBatchesList,
    createPromoIsLoading,
    createPromoIsSuccess,
    createPromoIsError,
    createPromoError,
    batchCompleted,
    toastOptions,
    toast,
    t,
  ]);

  const contextValue = useMemo(
    () => ({
      createPromoMutation,
      createPromoData,
      createPromoIsLoading,
      createPromoError,
      createPromoIsError,
      createPromoIsSuccess,
      loadingTransition,
      setLoadingTransition,
    }),
    [
      createPromoMutation,
      createPromoData,
      createPromoIsLoading,
      createPromoError,
      createPromoIsError,
      createPromoIsSuccess,
      loadingTransition,
      setLoadingTransition,
    ]
  );

  return (
    <CreatePromoCodeFormContext.Provider value={contextValue}>
      <Box as={Container} maxW="container.md" margin="0 auto">
        <Flex onClick={goBackToBatchesList} {...cancelReturnStyles}>
          <ChevronLeft size={16} />
          <Text>{t.cancelAndReturnText}</Text>
        </Flex>

        <Form
          {...createPromotionFormConfig({
            getFormState,
            defaultValues: formDetails,
            onSubmit: handleSubmitCreatePromotionForm,
            baseDataTestId,
            currentLang,
            t: (id: string) => (t as any)[id] || id,
          })}
        />
      </Box>
    </CreatePromoCodeFormContext.Provider>
  );
}
