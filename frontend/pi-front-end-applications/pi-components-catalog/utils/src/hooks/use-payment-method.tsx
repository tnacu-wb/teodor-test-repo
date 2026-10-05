'use client';

import { PaymentMethod } from '@whitbread-eos/api';
import { Dispatch, SetStateAction, useState } from 'react';

export default function usePaymentMethod(): [
  PaymentMethod,
  Dispatch<SetStateAction<PaymentMethod>>,
] {
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>({
    name: '',
    type: '',
    subType: '',
    order: 0,
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: false,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  });

  return [paymentMethod, setPaymentMethod];
}
