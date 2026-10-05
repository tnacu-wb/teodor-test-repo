import { Box, Flex, FlexProps, Text } from '@chakra-ui/react';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import { CreatePromotionCodeForm } from '@whitbread-eos/molecules';
import { PromoNotes } from '@whitbread-eos/molecules/dist/unique-promotions';
import { useCreatePromoCodeFormContext } from '@whitbread-eos/molecules/dist/unique-promotions/Create';
import { usePromoTranslation } from '@whitbread-eos/utils';
import React from 'react';
import {
  Control,
  FieldErrors,
  UseFormClearErrors,
  UseFormGetValues,
  UseFormSetValue,
} from 'react-hook-form';

interface FormField {
  name: string;
  label: string;
  type?: string;
}

interface CreatePromtionCodeFormDetailsProps {
  control: Control<any>;
  formField: FormField;
  errors: FieldErrors<any>;
  getValues: UseFormGetValues<any>;
  handleSetError: (name: string, error: { type: string; message: string }) => void;
  clearErrors: UseFormClearErrors<any>;
  setValue: UseFormSetValue<any>;
  handleSetValue?: UseFormSetValue<any>;
  handleClearErrors?: UseFormClearErrors<any>;
}

const CreatePromotionCodeFormDetails = ({
  control,
  formField,
  errors,
  getValues,
  handleSetError,
  clearErrors,
  setValue,
  handleSetValue,
  handleClearErrors,
}: CreatePromtionCodeFormDetailsProps) => {
  const t = usePromoTranslation();
  const { createPromoIsLoading, createPromoIsError, loadingTransition } =
    useCreatePromoCodeFormContext();
  const isGeneric = getValues()?.isGeneric;
  const notes = isGeneric
    ? [t.genericpromoNotesOne, t.genericpromoNotesTwo, t.genericpromoNotesThree]
    : [t.notesLineOne, t.notesLineTwo, t.notesLineThree];

  return (
    <Flex {...mainFormWrapperStyles}>
      <Text {...createPromoHeader}>{t.createNewBatchTitle}</Text>
      <Box
        {...formWrapperStyles}
        sx={{
          ...(createPromoIsLoading || loadingTransition
            ? {
                pointerEvents: 'none',
                opacity: 0.6,
                transition: 'filter 0.2s ease, opacity 0.2s ease',
                position: 'relative',
              }
            : {}),
        }}
      >
        {(createPromoIsLoading || loadingTransition) && !createPromoIsError && (
          <Box
            position="absolute"
            inset={0}
            display="flex"
            alignItems="center"
            justifyContent="center"
            zIndex={10}
          >
            <LoadingSpinner />
          </Box>
        )}
        <Text {...fillFormTextStyles}>{t.formTitle}</Text>
        <CreatePromotionCodeForm
          formField={formField}
          control={control}
          errors={errors}
          getValues={getValues}
          handleSetError={handleSetError}
          clearErrors={clearErrors}
          setValue={setValue}
          handleSetValue={handleSetValue}
          handleClearErrors={handleClearErrors}
        />
      </Box>
      <PromoNotes notes={notes} />
    </Flex>
  );
};

const mainFormWrapperStyles = {
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  alignItems: 'center',
  borderRadius: '8px',
  background: '#FFF',
  boxShadow: '0 2px 8px 0 rgba(0, 0, 0, 0.20)',
} as FlexProps;

const formWrapperStyles = {
  width: '90%',
  padding: { base: '1.375rem 0', md: '2.625rem 0' },
  maxWidth: '26.1875rem',
  margin: '0 auto',
};

const fillFormTextStyles = {
  color: '#000',
  fontSize: '0.875rem',
  fontStyle: 'normal',
  fontWeight: '500',
  lineHeight: '1.3125rem',
  marginBottom: { base: '1.375rem', md: '2.625rem' },
};

const createPromoHeader = {
  borderRadius: '8px 8px 0 0',
  borderBottom: '1px solid #CCC',
  background: '#00798E',
  width: '100%',
  color: '#FFF',
  fontSize: '1.5rem',
  fontStyle: 'normal',
  fontWeight: '700',
  padding: { base: 'var(--M, 1rem) 1.375rem', md: 'var(--L, 2rem) 2.625rem' },
  lineHeight: '1.8rem',
};

export default CreatePromotionCodeFormDetails;
