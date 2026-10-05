import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import {
  ButtonsType,
  FieldsType,
  Form,
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  FormProps,
} from '@whitbread-eos/atoms';
import * as yup from 'yup';

import { render, userEvent, waitFor } from '../../utils/test-utils';
import PhoneSelector from './PhoneSelector.component';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockResponse,
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockedCountries = [
  {
    countryCode: 'A',
    countryCodeISO: 'AT',
    countryLegend: 'Austria',
    passportRequired: true,
    dialingCode: '+43',
    flagImg: '/content/dam/global/flags/Austria.png',
  },
  {
    countryCode: 'AFG',
    countryCodeISO: 'AF',
    countryLegend: 'Afghanistan',
    passportRequired: true,
    dialingCode: '+93',
    flagImg: '/content/dam/global/flags/Afghanistan.png',
  },
  {
    countryCode: 'AG',
    countryCodeISO: 'AG',
    countryLegend: 'Antigua and Barbuda',
    passportRequired: true,
    dialingCode: '+268',
    flagImg: '/content/dam/global/flags/Antigua-and-Barbuda.png',
  },
  {
    countryCode: 'AI',
    countryCodeISO: 'AI',
    countryLegend: 'Anguilla',
    passportRequired: true,
    dialingCode: '+809',
    flagImg: '/content/dam/global/flags/Anguilla.png',
  },
  {
    countryCode: 'AL',
    countryCodeISO: 'AL',
    countryLegend: 'Albania',
    passportRequired: true,
    dialingCode: '+355',
    flagImg: '/content/dam/global/flags/Albania.png',
  },
];

const mockResponse = {
  isLoading: false,
  isError: false,
  countriesRequestSuccess: true,
  error: { message: '' },
  data: mockedCountries,
};

const Component = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'phone',
      label: 'Phone',
      Component: PhoneSelector,
      testid: 'Mobile-Selector',
      props: {
        showIcon: true,
      },
    },
  ];

  const buttons: ButtonsType[] = [
    {
      type: FORM_BUTTON_TYPES.SUBMIT,
      label: 'Submit',
      action: jest.fn(),
      styles: {
        marginBottom: '0',
      },
      props: {
        variant: 'primary',
        size: 'full',
      },
    },
  ];

  const props: FormProps = {
    elements: {
      buttons,
      fields,
    },
    defaultValues: {
      phone: '',
    },
    validationSchema: yup.object().shape({
      phone: yup
        .string()
        .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
          message: 'Invalid phone',
          excludeEmptyString: true,
        })
        .min(FORM_VALIDATIONS.PHONE.MIN, 'Min error')
        .max(FORM_VALIDATIONS.PHONE.MAX, 'Max error'),
    }),
  };

  return <Form {...props} />;
};

const ComponentWithPhoneSet = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'phone',
      label: 'Phone',
      Component: PhoneSelector,
      testid: 'Mobile-Selector',
      props: {
        showIcon: true,
      },
    },
  ];

  const buttons: ButtonsType[] = [
    {
      type: FORM_BUTTON_TYPES.SUBMIT,
      label: 'Submit',
      action: jest.fn(),
      styles: {
        marginBottom: '0',
      },
      props: {
        variant: 'primary',
        size: 'full',
      },
    },
  ];

  const props: FormProps = {
    elements: {
      buttons,
      fields,
    },
    defaultValues: {
      phone: '+40123456',
    },
    validationSchema: yup.object().shape({
      phone: yup
        .string()
        .matches(FORM_VALIDATIONS.PHONE.MATCHES, {
          message: 'Invalid phone',
          excludeEmptyString: true,
        })
        .min(FORM_VALIDATIONS.PHONE.MIN, 'Min error')
        .max(FORM_VALIDATIONS.PHONE.MAX, 'Max error'),
    }),
  };

  return <Form {...props} />;
};

describe('Phone Selector', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    const input = getByTestId('Mobile-Selector-phoneNumber');

    expect(input).toBeInTheDocument();
  });

  it('should allow phone number selection', async () => {
    const { getByTestId } = render(<Component />);
    const input = getByTestId('Mobile-Selector-phoneNumber');

    await act(async () => {
      await userEvent.type(input, '0743123123');
    });
    expect(input).toHaveValue('743123123');
  });

  it('should show an error message if a wrong phone number is added', async () => {
    const { getByRole, getByTestId, getByText } = render(<Component />);
    const input = getByTestId('Mobile-Selector-phoneNumber');
    const submitBtn = getByRole('button', { name: 'Submit' });

    await userEvent.type(input, 'asd');
    await userEvent.click(submitBtn);

    await waitFor(() => {
      expect(getByText('Invalid phone')).toBeInTheDocument();
    });
  });

  it('should set the phone number value without the country dialing code', async () => {
    const { getByTestId } = render(<ComponentWithPhoneSet />);
    const input = getByTestId('Mobile-Selector-phoneNumber');

    expect(input).toHaveValue('123456');
  });
});
