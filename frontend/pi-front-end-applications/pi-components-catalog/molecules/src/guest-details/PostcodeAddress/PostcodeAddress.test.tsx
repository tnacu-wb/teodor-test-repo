import '@testing-library/jest-dom';
import { ButtonsType, FieldsType, Form, FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import * as yup from 'yup';

import { render, userEvent, waitFor } from '../../utils/test-utils';
import PostcodeAddress from './PostcodeAddress.component';

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
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    invalidateQueries: jest.fn(),
  }),
}));

const mockCall = jest.fn();

const mockResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: { message: '' },
  data: {
    partialAddress: [
      {
        id: 'test',
        addressText: 'Chris 2 Bit Fix Ltd, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY',
      },
    ],
    formattedAddress: {
      companyName: null as string | null,
      addressLine1: 'Flat 2, Belgravia Court' as string,
      addressLine2: '33 Ebury Street' as string | null,
      addressLine3: '' as string | null,
      addressLine4: 'LONDON' as string | null,
      label: 'Flat 2, Belgravia Court, 33 Ebury Street, LONDON, SW1W 0NY' as string | null,
      postalCode: 'SW1W 0NY' as string,
      country: 'GB' as string | null,
    },
  },
};

const Component = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'InputPostCode',
      label: 'Postal Code',
      Component: PostcodeAddress,
      testid: 'PostcodeAddress',
      props: {
        showIcon: true,
        fieldName: '',
      },
    },
  ];

  const buttons: ButtonsType[] = [];

  const props: FormProps = {
    elements: {
      buttons,
      fields,
    },
    defaultValues: {
      InputPostCode: '',
    },
    validationSchema: yup.object().shape({
      postalCode: yup
        .string()
        .required('Required')
        .matches(
          RegExp(
            '([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})'
          ),
          'config.errorMessages.yourDetails.postcode.invalid'
        ),
    }),
  };

  return <Form {...props} />;
};

const BillingComponent = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'InputPostCode',
      label: 'Postal Code',
      Component: PostcodeAddress,
      testid: 'PostcodeAddress',
      props: {
        fieldName: 'billing',
      },
    },
  ];

  const buttons: ButtonsType[] = [];

  const props: FormProps = {
    elements: {
      buttons,
      fields,
    },
    defaultValues: {
      InputPostCode: '',
    },
    validationSchema: yup.object().shape({
      postalCode: yup
        .string()
        .required('Required')
        .matches(
          RegExp(
            '([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\\s?[0-9][A-Za-z]{2})'
          ),
          'config.errorMessages.yourDetails.postcode.invalid'
        ),
    }),
  };

  return <Form {...props} />;
};

const PostCodeUKComponent = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'InputPostCode',
      label: 'Postal Code',
      Component: PostcodeAddress,
      testid: 'PostcodeAddress',
      props: {
        showIcon: true,
        fieldName: '',
      },
    },
  ];

  const buttons: ButtonsType[] = [];

  const props: FormProps = {
    elements: {
      buttons,
      fields,
    },
    defaultValues: {
      InputPostCode: '',
    },
    validationSchema: yup.object().shape({
      postalCode: yup
        .string()
        .required('Required')
        .matches(
          RegExp(
            '(GIR ?0AA|[A-PR-UWYZ](d{1,2}|([A-HK-Y]d([0-9ABEHMNPRV-Y])?)|d[A-HJKPS-UW]) ?d[ABD-HJLNP-UW-Z]{2})$/i'
          ),
          'config.errorMessages.yourDetails.postcode.invalid'
        ),
    }),
  };
  return <Form {...props} />;
};
const PostCodeUKComponentDisabled = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'InputPostCode',
      label: 'Postal Code',
      isDisabled: true,
      Component: PostcodeAddress,
      testid: 'PostcodeAddress',
      props: {
        showIcon: true,
        fieldName: '',
      },
    },
  ];

  const buttons: ButtonsType[] = [];

  const props: FormProps = {
    elements: {
      buttons,
      fields,
    },
    defaultValues: {
      InputPostCode: '',
    },
    validationSchema: yup.object().shape({
      postalCode: yup
        .string()
        .required('Required')
        .matches(
          RegExp(
            '(GIR ?0AA|[A-PR-UWYZ](d{1,2}|([A-HK-Y]d([0-9ABEHMNPRV-Y])?)|d[A-HJKPS-UW]) ?d[ABD-HJLNP-UW-Z]{2})$/i'
          ),
          'config.errorMessages.yourDetails.postcode.invalid'
        ),
    }),
  };
  return <Form {...props} />;
};

describe('Postcode component', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    const input = getByTestId('input-InputPostCode');
    expect(input).toBeInTheDocument();
  });

  it('should render the component', () => {
    const { getByTestId } = render(<BillingComponent />);
    const input = getByTestId('input-InputPostCode');
    expect(input).toBeInTheDocument();
  });

  it('should check the button exists', () => {
    const { getByTestId } = render(<Component />);

    const button = getByTestId('PostcodeAddress-FindAddressBtn');
    expect(button).toBeInTheDocument();
  });

  it('should show the dropdown ', async () => {
    //TODO this test is causing the commit to fail
    const { getByTestId } = render(<Component />);
    const input = getByTestId('input-InputPostCode');
    const button = getByTestId('PostcodeAddress-FindAddressBtn');

    await userEvent.type(input, 'SW1W0NY');
    await userEvent.click(button);

    await waitFor(() => {
      expect(getByTestId('DropdownComp-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render the component disabled', () => {
    const { getByTestId } = render(<PostCodeUKComponentDisabled />);
    const input = getByTestId('input-InputPostCode');

    expect(input).toBeDisabled();

    userEvent.click(input);
    expect(mockCall).toBeCalledTimes(0);
  });

  it('should enable address lookup for postcode without space and lower case  ', async () => {
    const { getByTestId } = render(<PostCodeUKComponent />);
    const input = getByTestId('input-InputPostCode');
    const button = getByTestId('PostcodeAddress-FindAddressBtn');

    await userEvent.type(input, 'lu55xe');
    await userEvent.click(button);

    await waitFor(() => {
      expect(getByTestId('DropdownComp-Wrapper')).toBeInTheDocument();
    });
  });

  it('should enable address lookup for postcode with space ', async () => {
    const { getByTestId } = render(<PostCodeUKComponent />);
    const input = getByTestId('input-InputPostCode');
    const button = getByTestId('PostcodeAddress-FindAddressBtn');

    await userEvent.type(input, 'LU5 5XE');
    await userEvent.click(button);

    await waitFor(() => {
      expect(getByTestId('DropdownComp-Wrapper')).toBeInTheDocument();
    });
  });

  it('should not enable address lookup for invalid postcode ', async () => {
    mockResponse.data = {
      partialAddress: [],
      formattedAddress: {
        companyName: null,
        addressLine1: '',
        addressLine2: null,
        addressLine3: null,
        addressLine4: '',
        label: '',
        postalCode: '',
        country: '',
      },
    };
    const { getByTestId, getByText } = render(<PostCodeUKComponent />);
    const input = getByTestId('input-InputPostCode');
    const button = getByTestId('PostcodeAddress-FindAddressBtn');

    await userEvent.type(input, 'LU 55XE');
    await userEvent.click(button);

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.postcode.invalid')).toBeInTheDocument();
    });
  });

  it('should show an error message when the input is invalid ', async () => {
    mockResponse.data = {
      partialAddress: [],
      formattedAddress: {
        companyName: null,
        addressLine1: '',
        addressLine2: null,
        addressLine3: null,
        addressLine4: '',
        label: '',
        postalCode: '',
        country: '',
      },
    };
    mockResponse.isSuccess = false;
    mockResponse.isError = true;
    mockResponse.error.message = 'config.errorMessages.yourDetails.postcode.invalid';

    const { getByTestId, getByText } = render(<Component />);
    const input = getByTestId('input-InputPostCode');
    const button = getByTestId('PostcodeAddress-FindAddressBtn');

    await userEvent.type(input, 'testinvalid');
    await userEvent.click(button);

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.postcode.invalid')).toBeInTheDocument();
    });
  });
});
