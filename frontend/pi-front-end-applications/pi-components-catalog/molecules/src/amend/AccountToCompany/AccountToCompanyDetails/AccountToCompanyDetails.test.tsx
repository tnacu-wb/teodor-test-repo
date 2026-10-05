import '@testing-library/jest-dom';

import { render, screen } from '../../../utils/test-utils';
import AccountToCompanyDetails from './AccountToCompanyDetails.component';

const props = {
  data: {
    number: '123',
    name: 'Test name',
    address: 'main street',
    postcode: '987',
  },
};

describe('AccountToCompanyDetails component', () => {
  it('should render the component', () => {
    render(<AccountToCompanyDetails {...props} />);
    expect(screen.getByDisplayValue(props.data.number)).toBeInTheDocument();
    expect(screen.getByDisplayValue(props.data.name)).toBeInTheDocument();
    expect(screen.getByDisplayValue(props.data.address)).toBeInTheDocument();
    expect(screen.getByDisplayValue(props.data.postcode)).toBeInTheDocument();
  });
});
