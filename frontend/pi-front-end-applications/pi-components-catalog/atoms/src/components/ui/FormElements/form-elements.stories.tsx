import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { FormCheckbox, FormInput, FormPage, FormRadioGroup, FormSelect } from './index';

const iconPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

const meta: Meta<typeof FormPage> = {
  title: 'Shadcn/FormElements',
  component: FormPage,
  argTypes: {
    baseDataTestId: {
      description: 'Base test id prefix for FormPage internals.',
      control: 'text',
    },
    title: {
      description: 'Main heading for the form page.',
      control: 'text',
    },
    backIcon: {
      description: 'Back icon source for optional back action.',
      control: 'text',
    },
    backHref: {
      description: 'Optional href for back navigation.',
      control: 'text',
    },
    isCentered: {
      description: 'Whether content uses centered width layout.',
      control: 'boolean',
    },
    onBackClick: {
      description: 'Back action callback.',
      control: false,
    },
    className: {
      description: 'Additional class names for FormPage root.',
      control: 'text',
    },
    children: {
      description: 'Nested form controls and content.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Composite form primitives including page wrapper, input/select, checkbox and radio controls.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const DemoFormElements = () => {
  const [name, setName] = useState('');
  const [travelType, setTravelType] = useState('business');
  const [accepted, setAccepted] = useState(false);
  const [country, setCountry] = useState({ value: 'uk', displayValue: 'United Kingdom' });

  return (
    <FormPage baseDataTestId="IB-Form" title="Traveller Details">
      <div className="space-y-4">
        <FormInput
          id="traveller-name"
          name="travellerName"
          placeholder="Traveller name"
          value={name}
          onChange={(event) => setName(event.target.value)}
          errorIcon={iconPixel}
        />
        <FormSelect
          id="traveller-country"
          name="travellerCountry"
          placeholder="Country"
          disabled={false}
          value={country}
          onChange={(nextValue) => setCountry(nextValue)}
          errorIcon={iconPixel}
          arrowIcon={iconPixel}
          className=""
          options={[
            { value: 'uk', displayValue: 'United Kingdom' },
            { value: 'de', displayValue: 'Germany' },
          ]}
        />
        <FormRadioGroup
          name="travelType"
          selectedValue={travelType}
          onChange={(nextValue) => setTravelType(nextValue)}
          items={[
            { value: 'business', label: 'Business' },
            { value: 'leisure', label: 'Leisure' },
          ]}
        />
        <FormCheckbox
          id="terms"
          name="terms"
          label="I accept the booking terms"
          value={accepted}
          onChange={setAccepted}
          errorIcon={iconPixel}
        />
      </div>
    </FormPage>
  );
};

export const Default: Story = {
  args: {
    baseDataTestId: 'IB-Form',
    title: 'Traveller Details',
    isCentered: true,
  },
  render: () => <DemoFormElements />,
};
