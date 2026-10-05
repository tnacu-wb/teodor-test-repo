import { FORM_FIELD_TYPES } from '..';
import { Divider, Flex, Heading, Text } from '@chakra-ui/react';
import { restaurantFormAnalytics } from '@whitbread-eos/utils';
import { memo, useState } from 'react';

import FormDynamicField from '../../../Form/FormDynamicField/FormDynamicField.component';
import { FieldsType } from '../../../Form/formTypes';
// import { analytics } from '~utils/analytics';
import Button from '../../Button';
import Switcher from '../../Switcher';
import EnquiryForm from '../EnquiryForm/EnquiryForm';
import FormCheckbox from '../FormCheckbox';
import FormDropdown from '../FormDropDown';
import FormInput from '../FormInput';
import FormMenuDropdown from '../FormMenuDropdown/FormMenuDropdown.component';
import FormRelatedFields from '../FormRelatedFields/FormRelatedFields.component';
import FormSessionTabs from '../FormSessionTabs/FormSessionTabs.component';
import FormSingleDatePicker from '../FormSingleDatePicker/FormSingleDatePicker.component';
import FormTextarea from '../FormTextarea';
import { FormFieldProps } from '../formTypes';

const MemoizedFormSessionTabs = memo(FormSessionTabs);

function FormField({
  control,
  formField,
  childrenToggle,
  specialRequestLabel,
  optionalText,
  enquirySubheading,
  enquiryHeading,
  formStepOneCompleted,
  isEnquiry,
  setIsEnquiry,
  errors,
  handleSetError,
  handleClearErrors,
  handleTriggerValidation,
  clearErrors,
  getValues,
  setValue,
  handleSetValue,
  handleResetField,
  setIsMenuOptionAvailable,
  reset,
}: Readonly<FormFieldProps>) {
  const { type } = formField;
  const [isChildrenDropDownOpen, setIsChildrenDropDownOpen] = useState<boolean>(false);
  const [userInfoToggle, setUserInfoToggle] = useState<boolean>(false);
  if (formField.hidden === true) {
    return null;
  }
  function setAdditionalDetails(data: { isChecked: boolean }) {
    restaurantFormAnalytics.update({ additionalRequirements: data.isChecked });
    setUserInfoToggle(data.isChecked);
  }
  const selectedTimeSlot = getValues?.('time');

  const renderFieldType = () => {
    switch (type) {
      case FORM_FIELD_TYPES.INPUT_TEXT:
        return <FormInput control={control} formField={formField} errors={errors} />;

      case FORM_FIELD_TYPES.TEXT_AREA:
        return <FormTextarea control={control} formField={formField} />;

      case FORM_FIELD_TYPES.CHECKBOX:
        return <FormCheckbox control={control} formField={formField} errors={errors} />;

      case FORM_FIELD_TYPES.DROPDOWN:
        if (formField.name === 'children') {
          return (
            <>
              {!isChildrenDropDownOpen ? (
                <Button
                  variant="toggle"
                  display="flex"
                  justifyContent="flex-start"
                  mb="2xl"
                  size="contentFit"
                  mt="sm"
                  p="0"
                  cursor="pointer"
                  data-testid="childrenToggle"
                  color="black"
                  fontWeight="400"
                  onClick={() => {
                    setIsChildrenDropDownOpen(true);
                  }}
                >
                  <Text fontWeight="bold" mr="5px">
                    +
                  </Text>
                  <Text textDecoration="underline">{childrenToggle}</Text>
                  <Text ml="5px">({optionalText})</Text>
                </Button>
              ) : (
                <FormDropdown
                  getValues={getValues}
                  setValue={setValue}
                  isEnquiry={isEnquiry}
                  setIsEnquiry={setIsEnquiry}
                  formField={formField}
                  control={control}
                  errors={errors}
                />
              )}
            </>
          );
        } else
          return (
            <FormDropdown
              getValues={getValues}
              setValue={setValue}
              isEnquiry={isEnquiry}
              setIsEnquiry={setIsEnquiry}
              formField={formField}
              control={control}
              errors={errors}
            />
          );

      case FORM_FIELD_TYPES.DYNAMIC_FIELD:
        if (!getValues) return null;
        return (
          <>
            <FormDynamicField
              control={control}
              formField={formField as FieldsType}
              errors={errors}
              handleSetValue={handleSetValue}
              handleResetField={handleResetField}
              handleSetError={handleSetError}
              handleClearErrors={handleClearErrors}
              handleTriggerValidation={handleTriggerValidation}
              getValues={getValues}
              reset={reset}
            />
            {!!formField.relatedFields && getValues && (
              <FormRelatedFields
                fieldName={formField.name}
                relatedFields={formField.relatedFields}
                control={control}
                errors={errors}
                handleSetValue={handleSetValue}
                handleResetField={handleResetField}
                getValues={getValues}
                reset={reset}
              />
            )}
          </>
        );

      case FORM_FIELD_TYPES.SINGLE_DATE_PICKER:
        return (
          <FormSingleDatePicker
            isEnquiry={isEnquiry}
            setValue={setValue}
            control={control}
            formField={formField}
            errors={errors}
          />
        );

      case FORM_FIELD_TYPES.SESSION_TABS: {
        if (getValues?.('adults'))
          return (
            <MemoizedFormSessionTabs
              getValues={getValues}
              selectedTimeSlot={selectedTimeSlot}
              isEnquiry={isEnquiry}
              control={control}
              formField={formField}
              errors={errors}
              clearErrors={clearErrors}
              adult={getValues?.('adults')}
              childrenValue={getValues?.('children')}
              time={selectedTimeSlot}
              setValue={setValue}
              selectedDateValue={getValues?.('date')}
            />
          );
        return null;
      }

      case FORM_FIELD_TYPES.MENUDROPDOWN: {
        if (selectedTimeSlot)
          return (
            <FormMenuDropdown
              getValues={getValues}
              setValue={setValue}
              formField={formField}
              control={control}
              errors={errors}
              clearErrors={clearErrors}
              time={selectedTimeSlot}
              selectedDateValue={getValues?.('date')}
              setIsMenuOptionAvailable={setIsMenuOptionAvailable}
            />
          );
        return null;
      }

      case FORM_FIELD_TYPES.SWITCH: {
        return (
          <>
            <Flex {...formField.styles} align="center">
              <Switcher
                data-testid={formField.testid}
                onChange={(data) => setAdditionalDetails(data)}
                isChecked={userInfoToggle}
              />
              <Text ml="sm">{`${specialRequestLabel} (${optionalText})`}</Text>
            </Flex>
            {!!formField.relatedFields && userInfoToggle && (
              <FormRelatedFields
                fieldName={formField.name}
                relatedFields={formField.relatedFields}
                control={control}
                errors={errors}
                handleSetValue={handleSetValue}
                handleResetField={handleResetField}
                getValues={getValues}
              />
            )}
          </>
        );
      }

      case FORM_FIELD_TYPES.ENQUIRY_FORM_FIELDS: {
        if (isEnquiry)
          return (
            <EnquiryForm
              enquiryHeading={enquiryHeading}
              enquirySubheading={enquirySubheading}
              getValues={getValues}
              handleResetField={handleResetField}
              handleSetValue={handleSetValue}
              formField={formField}
              control={control}
              errors={errors}
            />
          );
        return null;
      }

      case FORM_FIELD_TYPES.USER_DETAILS:
        if (formStepOneCompleted)
          return (
            <Flex flexDirection={'column'}>
              <Divider mb="40px" orientation="horizontal" />
              <Heading fontSize="24px" fontWeight="700" mb="16px" color="#511E62">
                {formField?.label}
              </Heading>

              {!!formField.relatedFields && (
                <FormRelatedFields
                  fieldName={formField.name}
                  relatedFields={formField.relatedFields}
                  control={control}
                  errors={errors}
                  handleSetValue={handleSetValue}
                  handleResetField={handleResetField}
                  getValues={getValues}
                />
              )}
            </Flex>
          );
        return null;

      default:
        return null;
    }
  };

  return renderFieldType();
}
export default FormField;
