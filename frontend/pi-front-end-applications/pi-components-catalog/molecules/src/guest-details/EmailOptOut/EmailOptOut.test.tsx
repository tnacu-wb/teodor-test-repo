import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { FieldsType } from '@whitbread-eos/atoms/dist/components/Form/formTypes';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../utils/test-utils';
import EmailOptOut from './EmailOptOut.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  analytics: {
    update: jest.fn(),
  },
}));

const baseDataTestId = 'GuestDetails-AcceptFutureMailing';

const Component = () => {
  const { control } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'acceptFutureMailing',
    label: 'acceptFutureMailing',
    testid: baseDataTestId,
    props: {
      brand: 'PI',
      currentLang: 'de',
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    field: { name: 'acceptFutureMailing', value: '', onChange: jest.fn(), onBlur: jest.fn() },
  };

  return <EmailOptOut {...props} />;
};

const ComponentPIDHotel = () => {
  const { control } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'acceptFutureMailing',
    label: 'acceptFutureMailing',
    testid: baseDataTestId,
    props: {
      brand: 'PID',
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    field: { name: 'acceptFutureMailing', value: '', onChange: jest.fn(), onBlur: jest.fn() },
  };

  return <EmailOptOut {...props} />;
};

const ComponentEnglishAndGermanyAsCountry = () => {
  const { control } = useForm({
    defaultValues: {
      countryCode: 'DE',
    },
  });

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'acceptFutureMailing',
    label: 'acceptFutureMailing',
    testid: baseDataTestId,
    props: {
      brand: 'PI',
      currentLang: 'en',
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    field: { name: 'acceptFutureMailing', value: '', onChange: jest.fn(), onBlur: jest.fn() },
  };

  return <EmailOptOut {...props} />;
};

describe('Email Updates', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should check the Email preference option', async () => {
    const { getByRole } = render(<Component />);
    const checkbox = getByRole('checkbox');

    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
    await userEvent.click(checkbox);
  });

  it('should display same message for DE website', () => {
    const { getByText } = render(<Component />);
    expect(getByText('booking.emailTextGermanResidence.optOut')).toBeInTheDocument();
  });

  it('should display different message for PID brand hotel', () => {
    const { getByText } = render(<ComponentPIDHotel />);
    expect(getByText('booking.emailBoxText.premierinnhubzip')).toBeInTheDocument();
  });

  it('should display same message for EN website and Germany as country selected', () => {
    const { getByText } = render(<ComponentEnglishAndGermanyAsCountry />);
    expect(getByText('booking.emailBoxText.premierinnhubzip')).toBeInTheDocument();
  });

  it('should contain a checkbox for updating the email preference', async () => {
    const { getByRole } = render(<Component />);
    const checkbox = getByRole('checkbox', {
      name: 'booking.emailBoxText.GermanResidence.optOut',
    });
    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  describe('EmailOptOut component when isRemovePIIDataFromLocalStorageEnabled is true', () => {
    const Component = ({ defaultValue }: { defaultValue: boolean }) => {
      const { control } = useForm();

      const fieldType: FieldsType = {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        name: 'acceptFutureMailing',
        label: 'acceptFutureMailing',
        testid: baseDataTestId,
        props: {
          brand: 'PI',
          currentLang: 'en',
          isRemovePIIDataFromLocalStorageEnabled: true,
          defaultValues: {
            acceptFutureMailing: defaultValue,
          },
        },
      };

      const props: any = {
        control,
        formField: fieldType,
        field: { name: 'acceptFutureMailing', value: '', onChange: jest.fn(), onBlur: jest.fn() },
      };

      return <EmailOptOut {...props} />;
    };

    it('should set isChecked to false when isRemovePIIDataFromLocalStorageEnabled is true and defaultAcceptFutureMailing is true', () => {
      const { getByRole } = render(<Component defaultValue={true} />);
      const checkbox = getByRole('checkbox');

      expect(checkbox).not.toBeChecked();
    });

    it('should set isChecked to true when isRemovePIIDataFromLocalStorageEnabled is true and defaultAcceptFutureMailing is false', () => {
      const { getByRole } = render(<Component defaultValue={false} />);
      const checkbox = getByRole('checkbox');

      expect(checkbox).toBeChecked();
    });
  });
});
