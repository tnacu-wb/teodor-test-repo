import '@testing-library/jest-dom';
import { UserDataContext } from '@whitbread-eos/utils';
import React from 'react';

import { render } from '../../utils/test-utils';
import RegisterProfile from './RegisterProfile';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const useQueryResponse = {
  props: {
    currentLang: 'en',
    bkngData: {
      bookingConfirmation: {
        reservationByIdList: [
          {
            reservationBooker: {
              title: 'Mr',
              firstName: 'Steve',
              lastName: 'Jobs',
              address: {
                companyName: null,
                addressType: 'HOME',
                addressLine1: '123 Sansom Road',
                addressLine2: '',
                addressLine3: '',
                addressLine4: 'LONDON',
                cityName: null,
                countryCode: 'GB',
                postalCode: 'E11 3HG',
              },
              email: 'sj@apple.com',
              mobile: '+4407835138906',
              landline: null,
            },
          },
        ],
      },
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestQueryRequest: () => useQueryResponse,
}));

describe('Register Profile', () => {
  const handleSetValue = jest.fn();

  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should call handleSetValue and populate fields with values from the response (UK residence)', () => {
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile formField={useQueryResponse} handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledWith('title', 'Mr');
    expect(handleSetValue).toHaveBeenCalledWith('firstName', 'Steve');
    expect(handleSetValue).toHaveBeenCalledWith('lastName', 'Jobs');
    expect(handleSetValue).toHaveBeenCalledWith('email', 'sj@apple.com');
    expect(handleSetValue).toHaveBeenCalledWith('phone', '+4407835138906');
    expect(handleSetValue).toHaveBeenCalledWith('addressSelection', 'HOME');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine1', '123 Sansom Road');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine2', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine3', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine4', 'LONDON');
    expect(handleSetValue).toHaveBeenCalledWith('postalCode', 'E11 3HG');
    expect(handleSetValue).toHaveBeenCalledWith('countryCode', 'GB');
  });

  it('should call handleSetValue and populate fields with values from the response (Company)', () => {
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.address.companyName =
      'Apple';
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile formField={useQueryResponse} handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledWith('title', 'Mr');
    expect(handleSetValue).toHaveBeenCalledWith('firstName', 'Steve');
    expect(handleSetValue).toHaveBeenCalledWith('lastName', 'Jobs');
    expect(handleSetValue).toHaveBeenCalledWith('email', 'sj@apple.com');
    expect(handleSetValue).toHaveBeenCalledWith('phone', '+4407835138906');
    expect(handleSetValue).toHaveBeenCalledWith('addressSelection', 'HOME');
    expect(handleSetValue).toHaveBeenCalledWith('companyName', 'Apple');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine1', '123 Sansom Road');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine2', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine3', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine4', 'LONDON');
    expect(handleSetValue).toHaveBeenCalledWith('postalCode', 'E11 3HG');
    expect(handleSetValue).toHaveBeenCalledWith('countryCode', 'GB');
  });

  it('should call handleSetValue and populate fields with values from the response (Landline number)', () => {
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.landline =
      '+4408876445536';
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.mobile =
      null;

    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile formField={useQueryResponse} handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledWith('title', 'Mr');
    expect(handleSetValue).toHaveBeenCalledWith('firstName', 'Steve');
    expect(handleSetValue).toHaveBeenCalledWith('lastName', 'Jobs');
    expect(handleSetValue).toHaveBeenCalledWith('email', 'sj@apple.com');
    expect(handleSetValue).toHaveBeenCalledWith('phone', '+4408876445536');
    expect(handleSetValue).toHaveBeenCalledWith('addressSelection', 'HOME');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine1', '123 Sansom Road');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine2', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine3', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine4', 'LONDON');
    expect(handleSetValue).toHaveBeenCalledWith('postalCode', 'E11 3HG');
    expect(handleSetValue).toHaveBeenCalledWith('countryCode', 'GB');
  });

  it('should call handleSetValue and populate fields with values from the response (German residence)', () => {
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.address.addressLine4 =
      null;
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.address.cityName =
      'BERLIN';
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.address.countryCode =
      'DE';
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.address.postalCode =
      '12345';

    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile formField={useQueryResponse} handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledWith('title', 'Mr');
    expect(handleSetValue).toHaveBeenCalledWith('firstName', 'Steve');
    expect(handleSetValue).toHaveBeenCalledWith('lastName', 'Jobs');
    expect(handleSetValue).toHaveBeenCalledWith('email', 'sj@apple.com');
    expect(handleSetValue).toHaveBeenCalledWith('phone', '+4408876445536');
    expect(handleSetValue).toHaveBeenCalledWith('addressSelection', 'HOME');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine1', '123 Sansom Road');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine2', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine3', '');
    expect(handleSetValue).toHaveBeenCalledWith('cityName', 'BERLIN');
    expect(handleSetValue).toHaveBeenCalledWith('postalCode', '12345');
    expect(handleSetValue).toHaveBeenCalledWith('countryCode', 'DE');
  });

  it('should call handleSetValue and populate fields with values from the response and set the addressType to HOME for German website', () => {
    useQueryResponse.props.currentLang = 'de';
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker.address.addressType =
      'BUSINESS';

    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile formField={useQueryResponse} handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledWith('title', 'Mr');
    expect(handleSetValue).toHaveBeenCalledWith('firstName', 'Steve');
    expect(handleSetValue).toHaveBeenCalledWith('lastName', 'Jobs');
    expect(handleSetValue).toHaveBeenCalledWith('email', 'sj@apple.com');
    expect(handleSetValue).toHaveBeenCalledWith('phone', '+4408876445536');
    expect(handleSetValue).toHaveBeenCalledWith('addressSelection', 'HOME');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine1', '123 Sansom Road');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine2', '');
    expect(handleSetValue).toHaveBeenCalledWith('addressLine3', '');
    expect(handleSetValue).toHaveBeenCalledWith('cityName', 'BERLIN');
    expect(handleSetValue).toHaveBeenCalledWith('postalCode', '12345');
    expect(handleSetValue).toHaveBeenCalledWith('countryCode', 'DE');
  });

  it('should NOT populate fields', () => {
    const { queryByText } = render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile handleSetValue={handleSetValue} />
      </UserDataContext.Provider>
    );

    expect(queryByText('Mr')).not.toBeInTheDocument();
  });

  it('should NOT call handleSetValue', () => {
    useQueryResponse.props.bkngData.bookingConfirmation.reservationByIdList[0].reservationBooker =
      null;

    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <RegisterProfile formField={useQueryResponse} />
      </UserDataContext.Provider>
    );

    expect(handleSetValue).toHaveBeenCalledTimes(0);
  });
});
