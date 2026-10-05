import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useForm } from 'react-hook-form';

import { render } from '../../utils/test-utils';
import CancellationDate from './CancellationDate.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const fieldType: FieldsType = {
  type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
  name: 'cancellationDate',
  label: 'cancellationDate',
  testid: 'SearchBookingsPage-CancellationDate',
};

const Component = () => {
  const { control } = useForm();

  const props: any = {
    control,
    formField: fieldType,
  };

  return <CancellationDate {...props} />;
};

describe('Cancellation Date', () => {
  it('should render the component ', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId('SearchBookingsPage-CancellationDate-Container')).toBeInTheDocument();
  });

  it('should render the component with testid undefined', () => {
    fieldType.testid = undefined;
    const { getByTestId } = render(<Component />);
    expect(getByTestId('CancellationDate-Container')).toBeInTheDocument();
  });
});
