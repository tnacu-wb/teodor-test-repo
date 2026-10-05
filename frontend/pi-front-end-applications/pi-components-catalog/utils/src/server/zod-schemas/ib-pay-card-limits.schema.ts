import { FieldValues, UseFormClearErrors, UseFormSetError } from 'react-hook-form';
import { z } from 'zod';

export const IBPayCardLimitsSchema = (t: any) => {
  const schema = z.object({
    creditLimit: z.boolean(),
    creditLimitNumber: z.string().optional(),
    usageRestriction: z.boolean(),
    startDate: z.date().optional(),
    endDate: z.date().optional(),
  });

  const validation = (
    data: z.infer<typeof schema>,
    setError: UseFormSetError<FieldValues>,
    clearErrors: UseFormClearErrors<FieldValues>
  ) => {
    let hasErrors = false;
    if (data.creditLimit) {
      const limitNumber = Number(data.creditLimitNumber);
      if (
        !(
          data?.creditLimitNumber?.trim() &&
          !isNaN(limitNumber) &&
          limitNumber >= 0 &&
          Number.isInteger(limitNumber)
        )
      ) {
        hasErrors = true;
        setError('creditLimitNumber', {
          type: 'manual',
          message: t('cards.cardMgmt.cardDetails.limits.currency.error'),
        });
      } else {
        clearErrors('creditLimitNumber');
      }
    }
    if (data.usageRestriction && !data.startDate) {
      hasErrors = true;
      setError('startDate', {
        type: 'manual',
        message: t('cards.cardMgmt.cardDetails.limits.date.error'),
      });
    } else {
      clearErrors('startDate');
    }
    if (data.usageRestriction && !data.endDate) {
      hasErrors = true;
      setError('endDate', {
        type: 'manual',
        message: t('cards.cardMgmt.cardDetails.limits.date.error'),
      });
    } else {
      clearErrors('endDate');
    }
    return !hasErrors;
  };

  return { schema, validation };
};
