import '@testing-library/jest-dom';
import { UserDataContext } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import CompanyBillingProfile from './CompanyBillingProfile';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const useQueryResponse = {
  data: {
    billing_companyName: 'BMW',
    billing_addressLine1: 'Frankfurter Ring 35',
    billing_addressLine2: '',
    billing_addressLine3: '',
    billing_addressLine4: 'München',
    billing_postalCode: '80807',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestQueryRequest: () => useQueryResponse,
}));

describe('CompanyBillingProfile', () => {
  const handleSetValue = jest.fn();
  const formField = {
    props: {
      companyProfile: {
        name: 'BMW',
        address: {
          addressLine1: 'Frankfurter Ring 35',
          addressLine2: '',
          addressLine3: '',
          addressLine4: 'München',
          postalCode: '80807',
        },
      },
    },
  };

  it('should call handleSetValue with correct values', () => {
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: true,

          setIsLoggedIn: () => {},
        }}
      >
        <CompanyBillingProfile formField={formField} handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledWith('billing_addressSelection', 'BUSINESS');
    expect(handleSetValue).toHaveBeenCalledWith('billing_companyName', 'BMW');
    expect(handleSetValue).toHaveBeenCalledWith('billing_addressLine1', 'Frankfurter Ring 35');
    expect(handleSetValue).toHaveBeenCalledWith('billing_addressLine2', '');
    expect(handleSetValue).toHaveBeenCalledWith('billing_addressLine3', '');
    expect(handleSetValue).toHaveBeenCalledWith('billing_addressLine4', 'München');
    expect(handleSetValue).toHaveBeenCalledWith('billing_cityName', 'München');
    expect(handleSetValue).toHaveBeenCalledWith('billing_postalCode', '80807');
  });
});
