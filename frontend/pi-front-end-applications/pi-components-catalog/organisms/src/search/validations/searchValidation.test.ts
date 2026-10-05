import { add } from 'date-fns';

import { validateSearchData } from './searchValidation';

const dataTranslations = {
  headerInformation: {
    roomCodes: {
      accessible: 'DIS',
      double: 'DB',
      family: 'FAM',
      single: 'SB',
      twin: 'TWIN',
    },
  },
};

const errDateInTheFutureMock = {
  errorKey: ['arrivalDateInTheFuture', 'datepicker'],
};

const errOcupancyResulttMock = {
  errorKey: ['invalidOccupancy', 'occupancy'],
};

const errArrivalDateResultMock = {
  errorKey: ['arrivalDateInThePast', 'datepicker'],
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  swapKeysAndValues: jest.fn().mockReturnValue({
    DIS: 'accessible',
    DB: 'double',
    FAM: 'family',
    SB: 'single',
    TWIN: 'twin',
  }),
  logicalOrOperator: jest.fn().mockImplementation((a, b) => a || b),
}));

const mockRouter = {
  push: jest.fn(),
  query: {
    ARRdd: '1',
    ARRmm: '11',
    ARRyyyy: '2025',
    NIGHTS: '2',
    ROOMS: '1',
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'DB',
    slug: ['england', 'greater-london', 'london', 'london-beckton.html'],
  },
  asPath:
    '/en/hotels/england/greater-london/london/london-beckton.html?ARRdd=1&ARRmm=11&ARRyyyy=2025&NIGHTS=2&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB',
};
(mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

const router: any = mockRouter;

const mockedActualYear = new Date().getFullYear();

describe('Validations functions test', () => {
  beforeAll(() => {
    jest.clearAllMocks();
  });
  it('call validateSearchData with a valid data in the future and match the result', () => {
    const currentDate = new Date();
    const futureDate = add(currentDate, {
      years: 1,
      months: 1,
      days: 5,
    });

    const futureDay = futureDate.getDate();
    const futureMonth = futureDate.getMonth() + 1;
    const futureYear = futureDate.getFullYear();

    const result = validateSearchData(
      futureDay,
      futureMonth,
      futureYear,
      2,
      null,
      dataTranslations,
      router,
      364,
      9,
      [
        {
          adults: 1,
          children: 0,
          shouldIncludeCot: false,
          roomType: 'DB',
        },
      ]
    );
    expect(result.errorKey).toEqual(errDateInTheFutureMock.errorKey);
  });
  it('call validateSearchData with invalid occupancy and throw invalid occupancy error', () => {
    const result = validateSearchData(
      31,
      12,
      mockedActualYear,
      2,
      null,
      dataTranslations,
      router,
      364,
      9,
      undefined
    );
    expect(result.errorKey).toEqual(errOcupancyResulttMock.errorKey);
  });
  it('call validateSearchData with past date and throw arrival date in the past error', () => {
    const result = validateSearchData(
      5,
      11,
      2021,
      2,
      null,
      dataTranslations,
      router,
      364,
      9,
      undefined
    );
    expect(result.errorKey).toEqual(errArrivalDateResultMock.errorKey);
  });
});
