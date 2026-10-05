import { Box, FormLabel } from '@chakra-ui/react';
import { formatAssetsUrl, akamaiImageLoader, isIVMEnabled } from '@whitbread-eos/utils';
import Image from 'next/image';
import Select, { GroupBase, OptionsOrGroups, StylesConfig } from 'react-select';

interface Props {
  dataTestId?: string;
  styles: StylesConfig;
  options: OptionsOrGroups<unknown, GroupBase<unknown>>;
  value: unknown;
  placeholder: string;
  onChange: (...event: any[]) => void;
  name?: string;
  isMulti?: boolean;
  isClearable?: boolean;
  closeMenuOnSelect?: boolean;
  isDisabled?: boolean;
}

const Multiselect = ({
  name,
  dataTestId,
  options,
  styles,
  value,
  onChange,
  placeholder,
  isMulti,
  isClearable,
  closeMenuOnSelect,
  isDisabled,
}: Props) => (
  <Box position="relative">
    <FormLabel
      data-testid={`${dataTestId}-label`}
      pos="absolute"
      {...labelStyle(value)}
      htmlFor={name}
    >
      {placeholder}
    </FormLabel>
    <Select
      data-testid={`${dataTestId}-multiSelect`}
      options={options}
      value={value}
      name={name}
      onChange={onChange}
      styles={styles}
      closeMenuOnSelect={closeMenuOnSelect}
      placeholder={placeholder}
      formatOptionLabel={CustomOption}
      isMulti={isMulti}
      isClearable={isClearable}
      isDisabled={isDisabled}
      hideSelectedOptions
      components={{ IndicatorSeparator: () => null, ClearIndicator: () => null }}
    />
  </Box>
);

export default Multiselect;

const CustomOption = ({ label, image, value }: any) => (
  <div style={{ display: 'flex', alignItems: 'center' }}>
    {image && (
      <Image
        src={formatAssetsUrl(image)}
        width="24"
        height="24"
        alt={`multi-select-img-${value}`}
        loader={isIVMEnabled() ? akamaiImageLoader : undefined}
      />
    )}
    <label style={{ marginLeft: '10px' }}>{label}</label>
  </div>
);

const labelStyle = (value: string | undefined | unknown, error = '') => {
  return {
    h: '1.25rem',
    w: 'fit-content',
    fontSize: 'sm',
    align: 'center',
    px: 'xs',
    fontWeight: 'normal',
    ml: '0.750rem',
    top: '-0.5rem',
    backgroundColor: 'baseWhite',
    zIndex: '1',
    color: error ? 'error' : 'darkGrey1',
    display: value ? 'block' : 'none',
    _focus: { color: error ? 'error' : 'primary', display: 'block' },
    lineHeight: '1',
  };
};
