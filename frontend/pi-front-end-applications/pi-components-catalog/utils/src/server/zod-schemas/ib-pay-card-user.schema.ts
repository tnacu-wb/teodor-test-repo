import { cardDisplayNameOptions } from '@whitbread-eos/api';
import { FieldValues, UseFormSetError, UseFormClearErrors } from 'react-hook-form';
import { z } from 'zod';

export const IBPayCardUserSchema = (t: any) => {
  const schema = z.object({
    user: z.string(),
    cardDisplayNameOption: z.object({
      displayValue: z.string(),
      value: z.string(),
    }),
    cardDisplayName: z.string().optional(),
    employeeId: z.string(),
    tetheredGuid: z.string(),
    schemeCustomerId: z.number(),
    schemeCountry: z.string(),
    apiUserGuid: z.string(),
  });

  const validation = (
    data: z.infer<typeof schema>,
    setError: UseFormSetError<FieldValues>,
    clearErrors: UseFormClearErrors<FieldValues>
  ) => {
    if (
      data.cardDisplayNameOption.value === cardDisplayNameOptions.custom &&
      !(
        data.cardDisplayName &&
        data.cardDisplayName.length >= 1 &&
        data.cardDisplayName.length <= 27 &&
        /^[\p{L} \-']*$/u.test(data.cardDisplayName)
      )
    ) {
      setError('cardDisplayName', {
        type: 'manual',
        message: t('cards.cardMgmt.addCard.displayName.typeName.error'),
      });
      return false;
    }
    clearErrors('cardDisplayName');
    return true;
  };
  return { schema, validation };
};
