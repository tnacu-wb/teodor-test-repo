import { Flex, FlexProps, StyleProps, Button } from '@chakra-ui/react';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction } from 'react';

enum SUBMIT_TYPE {
  SAVE = 'save',
  SUBMIT = 'submit',
}

interface FooterProps {
  setSubmitType: Dispatch<SetStateAction<SUBMIT_TYPE>>;
  isGerman: boolean;
}

const Footer = ({ setSubmitType, isGerman }: FooterProps) => {
  const { t } = useTranslation();

  const handleFormSubmit = (type: SUBMIT_TYPE) => {
    setSubmitType(type);
    dispatchFormSubmitEvent();
  };

  return (
    <Flex {...wraperBoxStyles}>
      <Button
        {...btnStyles}
        data-testid="submit-reg-form"
        id="reg-form-save-btn"
        onClick={() => handleFormSubmit(SUBMIT_TYPE.SAVE)}
      >
        {t('precheckin.guestdetails.savebutton')}
      </Button>
      <Button
        {...btnStyles}
        data-testid="reg-form-submit-btn"
        id="reg-form-submit-btn"
        onClick={() => handleFormSubmit(SUBMIT_TYPE.SUBMIT)}
      >
        {t(isGerman ? 'precheckin.details.submit' : 'precheckin.guestregistration.completebutton')}
      </Button>
    </Flex>
  );
};

export default Footer;

const wraperBoxStyles = {
  direction: { base: 'column', md: 'row' },
  alignItems: { base: 'center', md: 'flex-end' },
  justifyContent: { base: 'center', md: 'flex-end' },
  gap: { base: '2em', md: '2.3em' },
  mt: '3xl',
  mb: '0',
  w: { base: '100%', md: '100%' },
} as FlexProps;

const btnStyles = {
  w: { base: '100%', md: '17.72rem' },
} as StyleProps;

export function dispatchFormSubmitEvent() {
  const form = document.getElementById('preCheckInRegistrationForm') as HTMLFormElement | null;
  setTimeout(() => {
    form?.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
  }, 100);
}
