import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { MainContactNotification } from './main-contact-notification';

describe('MainContactNotification', () => {
  const defaultProps = {
    locale: LOCALES.EN,
    hasCompanyDetailsAccess: true,
    companyDetails: {
      requestedCompany: {
        companyDetails: {
          mainEmployee: {
            title: '',
            firstName: 'John',
            lastName: 'Doe',
            emailAddress: 'test@test.com',
            address: {
              addressLine1: 'AddressLine1',
              addressLine2: 'AddressLine2',
              addressLine3: 'AddressLine3',
              postCode: 'BL0 EO2',
              country: 'GB',
            },
          },
        },
      },
    },
  };

  it('renders incomplete main contact alert', async () => {
    render(<MainContactNotification {...defaultProps} />);
    expect(screen.getByTestId('Notifications-IncompleteMainEmployee')).toBeInTheDocument();
  });

  it('does not render incomplete main contact alert when mainEmployee is valid', async () => {
    const updatedProps = {
      ...defaultProps,
      companyDetails: {
        ...defaultProps.companyDetails,
        requestedCompany: {
          ...defaultProps.companyDetails.requestedCompany,
          companyDetails: {
            ...defaultProps.companyDetails.requestedCompany.companyDetails,
            mainEmployee: {
              ...defaultProps.companyDetails.requestedCompany.companyDetails.mainEmployee,
              title: 'Mr',
            },
          },
        },
      },
    };
    render(<MainContactNotification {...updatedProps} />);
    expect(screen.queryByTestId('Notifications-IncompleteMainEmployee')).not.toBeInTheDocument();
  });

  it('does not render incomplete main contact alert when user cannot access company details', async () => {
    const updatedProps = {
      ...defaultProps,
      hasCompanyDetailsAccess: false,
    };
    render(<MainContactNotification {...updatedProps} />);
    expect(screen.queryByTestId('Notifications-IncompleteMainEmployee')).not.toBeInTheDocument();
  });
});
