import { Dependents, PersonalDetails, PrivacyPolicy } from '@whitbread-eos/molecules';
import { GLOBALS } from '@whitbread-eos/utils';

import Footer from './FormFooter';

function PreCheckInForm({ control, formField, errors, getValues, handleSetValue }: any) {
  const { setSubmitType } = formField.props;

  return (
    <>
      <PersonalDetails
        formField={formField}
        control={control}
        errors={errors}
        getValues={getValues}
        handleSetValue={handleSetValue}
      />
      <Dependents
        formField={formField}
        control={control}
        errors={errors}
        getValues={getValues}
        handleSetValue={handleSetValue}
      />
      <PrivacyPolicy />
      <Footer
        setSubmitType={setSubmitType}
        isGerman={getValues && getValues()?.nationality?.value === GLOBALS.localeUpper.DE}
      />
    </>
  );
}

export default PreCheckInForm;
