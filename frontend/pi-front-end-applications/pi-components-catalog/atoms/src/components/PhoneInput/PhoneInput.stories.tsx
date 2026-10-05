import { Stack } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import { ComponentProps, SetStateAction, useState } from 'react';

import mockedCountries from './CountriesList/mocks/countries.json';
import type { PhoneValue } from './PhoneInput.component';
import PhoneInput from './PhoneInput.component';

type PhoneInputProps = ComponentProps<typeof PhoneInput>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

function PhoneInputWithState(props: PhoneInputProps) {
  const [value, setValue] = useState<PhoneValue>(
    props.value ?? {
      countryCode: '',
      dialingCode: '',
      phone: '',
    }
  );

  return (
    <Stack w="31rem">
      <PhoneInput
        {...props}
        value={props.value ?? value}
        onChange={(nextValue: SetStateAction<PhoneValue>) => {
          setValue(nextValue);
          props.onChange?.(nextValue);
        }}
        onBlur={props.onBlur ?? noop}
        formatAssetsUrl={props.formatAssetsUrl ?? ((path: string) => path)}
      />
    </Stack>
  );
}

const meta: Meta<typeof PhoneInput> = {
  title: 'PhoneInput',
  component: PhoneInput,
  argTypes: {
    countries: {
      description: 'Country options used by the country code selector',
      control: 'object',
    },
    disabled: {
      description: 'Disables both country selector and phone input when true',
      control: 'boolean',
    },
    error: {
      description: 'Validation error message displayed below the field or in tooltip',
      control: 'text',
    },
    formatAssetsUrl: {
      description: 'Function that transforms country flag asset URLs before rendering',
      control: false,
    },
    showIcon: {
      description: 'Displays error/success status icon inside the input',
      control: 'boolean',
    },
    inputProps: {
      description: 'Additional Chakra Input props passed to the phone input element',
      control: 'object',
    },
    label: {
      description: 'Field label shown above or inside the input depending on style',
      control: 'text',
    },
    placeholder: {
      description: 'Placeholder text shown when no value is entered',
      control: 'text',
    },
    currentLang: {
      description: 'Language key used to prioritise the default country in the list',
      control: 'radio',
      options: ['en', 'de'],
    },
    selectProps: {
      description: 'Additional countries list configuration excluding options and onChange',
      control: 'object',
    },
    name: {
      description: 'Input name/id used for form integration and accessibility',
      control: 'text',
    },
    value: {
      description: 'Current phone input value object with country and phone data',
      control: 'object',
    },
    onChange: {
      description: 'Callback fired whenever country or phone number changes',
      control: false,
    },
    onBlur: {
      description: 'Callback fired when the phone input loses focus',
      control: false,
    },
    handleTriggerValidation: {
      description: 'Optional callback used to trigger validation for dependent fields',
      control: false,
    },
    dependantOn: {
      description: 'Dependent field name(s) passed to validation trigger callback',
      control: 'object',
    },
    dataTestId: {
      description: 'Optional base data-testid value for QA selectors',
      control: 'text',
    },
    useTooltip: {
      description: 'Displays validation error using inline tooltip style',
      control: 'boolean',
    },
    isAltStyle: {
      description: 'Enables alternate visual style for label and input presentation',
      control: 'boolean',
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Phone number field with country selector, validation support, and optional alternate styling for compact forms.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const defaultArgs: PhoneInputProps = {
  name: 'phone',
  countries: mockedCountries,
  error: '',
  label: 'Phone number',
  placeholder: 'Your phone',
  onBlur: noop,
  onChange: noop,
};

// --- Basic Usage ---

export const Default: Story = {
  args: defaultArgs,
  render: (args) => <PhoneInputWithState {...args} />,
};

// --- Variants ---

export const GermanVariant: Story = {
  args: {
    ...Default.args,
    currentLang: 'de',
    placeholder: 'dein Telefon',
  },
};

// --- States ---

export const DisabledState: Story = {
  args: {
    ...Default.args,
    disabled: true,
  },
};

export const ErrorState: Story = {
  args: {
    ...Default.args,
    error: 'Please enter a valid phone number',
  },
};

// --- Configuration ---

export const WithTooltipError: Story = {
  args: {
    ...ErrorState.args,
    useTooltip: true,
  },
};

export const WithAlternateStyle: Story = {
  args: {
    ...Default.args,
    isAltStyle: true,
    label: 'Mobile number',
  },
};

// --- Edge Cases ---

export const WithEmptyCountryList: Story = {
  args: {
    ...Default.args,
    countries: [],
  },
};

export const WithLongPlaceholder: Story = {
  args: {
    ...Default.args,
    placeholder: 'Please enter your primary mobile phone number including all local digits',
  },
};
