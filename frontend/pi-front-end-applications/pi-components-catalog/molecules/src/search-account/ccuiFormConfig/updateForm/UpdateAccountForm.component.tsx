import { GridItem } from '@chakra-ui/react';
import { DataForUpdateFormType, SelectedRowType } from '@whitbread-eos/api';
import { Form, FormProps } from '@whitbread-eos/atoms';
import { useCustomLocale, formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useCallback, SetStateAction, useState, useEffect } from 'react';

import { updateAccountFormConfig } from './updateAccountFormConfig';

interface UpdateAccountFormArgsType {
  dataForUpdateForm: DataForUpdateFormType;
  selectedRow: SelectedRowType;
}

export default function UpdateAccountForm({
  dataForUpdateForm,
  selectedRow,
}: Readonly<UpdateAccountFormArgsType>) {
  const router = useRouter();

  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const reservationId = router.query?.reservationId;
  const reservationIdValue = typeof reservationId === 'string' ? reservationId : undefined;

  /* istanbul ignore next */ // To be removed after implementation
  const onSubmit = () => {
    // To be done in future implementations
    console.log('CTA-Submit-pressed');
  };

  const onGuestVerify = () => {
    setGuestNotVerified(false);
  };

  /* istanbul ignore next */ // To be removed after implementation
  const onUnlock = () => {
    // To be done in future implementations
    console.log('CTA-Reset-pressed');
  };

  /* istanbul ignore next */ // To be removed after implementation
  const onResetPassword = () => {
    // To be done in future implementations
    console.log('CTA-Reset-pressed');
  };

  const onReuseDetails = () => {
    const storedData = localStorage.getItem('formDetails');
    const defaultDataFromBooking = storedData ? JSON.parse(storedData) : null;
    if (reservationId) {
      if (defaultDataFromBooking) {
        const fallbackCityName =
          defaultDataFromBooking?.cityName || defaultDataFromBooking?.addressLine4 || '';
        try {
          window.localStorage.setItem(
            'formDetails',
            JSON.stringify({
              ...defaultDataFromBooking,
              // The value for "address" is not yet received
              title: defaultValues.title,
              firstName: defaultValues.firstName,
              lastName: defaultValues.lastName,
              email: defaultValues.email,
              phone: defaultValues.mobileNumber,
              landline: defaultValues.landlineNumber,
              companyName: defaultValues.companyName || '',
              postcodeAddress: defaultValues.postalCode, // It's used Home Address
              cityName: fallbackCityName,
            })
          );
        } catch (error) {
          // eslint-disable-next-line no-console
          console.log(error);
        }
      }
      window.location.href = `/${country}/${language}/guest-details?reservationId=${reservationId}`;
    }
  };
  /**
   * New Form Params
   */

  const [guestNotVerified, setGuestNotVerified] = useState<boolean>(true);
  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    title: dataForUpdateForm.title,
    firstName: dataForUpdateForm.firstName,
    lastName: dataForUpdateForm.lastName,
    companyName: dataForUpdateForm.companyName,
    email: dataForUpdateForm.email,
    address: dataForUpdateForm.address,
    postalCode: dataForUpdateForm.postalCode,
    mobileNumber: dataForUpdateForm.mobileNumber,
    landlineNumber: dataForUpdateForm.landlineNumber,
  });

  useEffect(() => {
    setDefaultValues(dataForUpdateForm);
    setGuestNotVerified(true);
  }, [selectedRow?.accountId]);

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );
  const baseDataTestId = 'UpdateAccountPage';

  return (
    <GridItem data-testid={formatDataTestId(baseDataTestId, 'wrapper')}>
      <Form
        {...updateAccountFormConfig({
          getFormState,
          defaultValues,
          onSubmit,
          onGuestVerify,
          onUnlock,
          onResetPassword,
          onReuseDetails,
          baseDataTestId,
          t,
          language,
          fieldsetDisabled: guestNotVerified,
          reservationId: reservationIdValue,
        })}
      ></Form>
    </GridItem>
  );
}
