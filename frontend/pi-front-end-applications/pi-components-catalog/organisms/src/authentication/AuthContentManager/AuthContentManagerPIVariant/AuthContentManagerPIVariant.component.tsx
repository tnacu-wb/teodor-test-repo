import { type AuthenticationLabels } from '@whitbread-eos/api';
import type { HeaderInformationData } from '@whitbread-eos/api';
import { type FormProps, ModalVariants } from '@whitbread-eos/atoms';
import { type SetStateAction, useCallback, useState } from 'react';
import type { FieldErrors } from 'react-hook-form';

import { LoginPIVariant } from '../../LogIn';
import { ResetPasswordPIVariant } from '../../ResetPassword';

export interface Props {
  isLoginModalOpen: boolean;
  toggleLoginModal: () => void;
  labels: AuthenticationLabels;
  headerInfoData?: HeaderInformationData['headerInformation'];
}

export default function AuthContentManagerPIVariant({
  isLoginModalOpen,
  toggleLoginModal,
  labels,
  headerInfoData,
}: Readonly<Props>) {
  const [isLoginForm, setIsLoginForm] = useState(true);
  const [loginDefaultValues, setLoginDefaultValues] = useState({
    email: '',
    password: '',
  });
  const [loginDefaultErrors, setLoginDefaultErrors] = useState({});
  const getLoginFormState: FormProps['getFormState'] = useCallback(
    (state1: any, errors1: any) => {
      setLoginDefaultValues(state1 as SetStateAction<{ email: string; password: string }>);
      setLoginDefaultErrors(errors1 as FieldErrors<{ email: string; password: string }>);
    },
    [setLoginDefaultValues, setLoginDefaultErrors]
  );

  const [resetPasswordDefaultValues, setResetPasswordDefaultValues] = useState({
    email: '',
  });
  const [resetPasswordDefaultErrors, setResetPasswordDefaultErrors] = useState({});

  const getResetPasswordFormState: FormProps['getFormState'] = useCallback(
    (state: any, errors: any) => {
      setResetPasswordDefaultValues(state as SetStateAction<{ email: string }>);
      setResetPasswordDefaultErrors(errors as FieldErrors<{ email: ''; password: string }>);
    },
    [setResetPasswordDefaultValues, setResetPasswordDefaultErrors]
  );

  return (
    <ModalVariants
      onClose={toggleLoginModal}
      isOpen={isLoginModalOpen}
      variant="default"
      variantProps={{ title: '', delimiter: true, sizeSm: 'full' }}
      updatedWidth={{ md: 'auto', sm: 'full' }}
      dataTestId={'Header-Auth'}
    >
      {isLoginForm ? (
        <LoginPIVariant
          setIsLoginForm={setIsLoginForm}
          defaultValues={loginDefaultValues}
          defaultErrors={loginDefaultErrors}
          getFormState={getLoginFormState}
          toggleLoginModal={toggleLoginModal}
          labels={labels}
          headerInfoData={headerInfoData}
        />
      ) : (
        <ResetPasswordPIVariant
          setIsLoginForm={setIsLoginForm}
          defaultValues={resetPasswordDefaultValues}
          defaultErrors={resetPasswordDefaultErrors}
          getFormState={getResetPasswordFormState}
          labels={labels}
        />
      )}
    </ModalVariants>
  );
}
