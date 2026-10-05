import { type FormDynamicFieldCompProps } from '@whitbread-eos/atoms';

import CountrySelector from './CountrySelector';
import CountrySelectorFilterable from './CountrySelectorFilterable';

export default function CountriesDropdown({
  formField,
  field,
  errors,
}: Readonly<FormDynamicFieldCompProps> & { isCountrySelectorFilterableEnabled?: boolean }) {
  const showIcon = formField.props?.showIcon;
  const setIsLocationRequired = formField.props?.setIsLocationRequired;
  const styles = formField.props?.styles;

  const onChange = (countryId: string) => {
    if (typeof setIsLocationRequired === 'function') {
      setIsLocationRequired(countryId === 'DE');
    }
    field.onChange(countryId);
  };

  return formField.props?.isCountrySelectorFilterableEnabled ? (
    <CountrySelectorFilterable
      field={field}
      getValues={() => {
        return field.value;
      }}
      formField={formField}
      selectedId={field.value}
      setIsLocationRequired={setIsLocationRequired}
    />
  ) : (
    <CountrySelector
      onChange={onChange}
      hasError={!!errors?.[formField.name]?.message?.length}
      selectedId={field.value}
      showIcon={showIcon}
      styles={styles}
      isDisabled={formField.isDisabled ?? false}
    />
  );
}
