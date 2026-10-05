import { type AuthenticationLabels } from '@whitbread-eos/api';
import { type FormProps, ModalVariants } from '@whitbread-eos/atoms';
import { type SetStateAction, useCallback, useState } from 'react';
import type { FieldErrors } from 'react-hook-form';

import { LogInBBVariant } from '../../LogIn';
import { ResetPasswordBBVariant } from '../../ResetPassword';

export interface Props {
  isLoginModalOpen: boolean;
  showRegisterNotification?: boolean;
  hasRegisteredSuccessfully?: boolean;
  toggleLoginModal: () => void;
  labels: AuthenticationLabels;
  onGoBack?: () => void;
  goBackButtonText?: string;
}

export default function AuthContentManagerBBVariant({
  isLoginModalOpen,
  showRegisterNotification,
  hasRegisteredSuccessfully,
  toggleLoginModal,
  labels,
  onGoBack,
  goBackButtonText,
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
      isOpen={isLoginModalOpen}
      onClose={toggleLoginModal}
      closeOnOverlayClick={false}
      variant="login"
      variantProps={{
        title: '',
        delimiter: true,
        onGoBack,
        goBackButtonText,
      }}
      dataTestId={'BB-Header-Auth'}
    >
      {isLoginForm ? (
        <LogInBBVariant
          setIsLoginForm={setIsLoginForm}
          defaultValues={loginDefaultValues}
          defaultErrors={loginDefaultErrors}
          getFormState={getLoginFormState}
          toggleLoginModal={toggleLoginModal}
          showRegisterNotification={showRegisterNotification}
          hasRegisteredSuccessfully={hasRegisteredSuccessfully}
          labels={labels}
        />
      ) : (
        <ResetPasswordBBVariant
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
