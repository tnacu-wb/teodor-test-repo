import { z } from 'zod';

export const cardLabelSchema = (t: any) => {
  return z.object({
    cardLabel: z
      .string()
      .min(1, t('cards.centrallyStoredCard.card.label.required'))
      .max(20, t('cards.centrallyStoredCard.card.label.required')),
  });
};
