import { PAYMENT_TYPES } from '@whitbread-eos/api';
import { FieldValues, UseFormClearErrors, UseFormSetError } from 'react-hook-form';
import { z } from 'zod';

export const cardTypeSchema = (t: any) => {
  const schema = z.object({
    cardType: z.string(),
    CNP: z.boolean(),
    memorableWord: z.string().optional(),
  });

  const validation = (
    data: z.infer<typeof schema>,
    setError: UseFormSetError<FieldValues>,
    clearErrors: UseFormClearErrors<FieldValues>
  ) => {
    if (
      (data.cardType === PAYMENT_TYPES.NEW_PIBA || data.cardType === PAYMENT_TYPES.KEEP_PIBA) &&
      data.CNP &&
      !data.memorableWord
    ) {
      setError('memorableWord', {
        type: 'manual',
        message: t('cards.centrallyStoredCard.card.memorableWord.required'),
      });
      return false;
    }
    clearErrors('memorableWord');
    return true;
  };

  return {
    schema,
    validation,
  };
};
