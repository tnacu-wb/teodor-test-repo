import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import { Analytics } from './Analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
    remove: jest.fn(),
  },
}));

const mockCompanyDetails = {
  requestedCompany: {
    companyDetails: {
      numberOfEmployees: 42,
      companyName: 'Test Company',
      companyAddress: {
        addressLine1: '123 Test St',
        postCode: 'AB12 3CD',
      },
      mainEmployee: {
        emailAddress: 'test@test.com',
        firstName: 'John',
        lastName: 'Doe',
      },
    },
  },
};

describe('Analytics Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    Object.defineProperty(window, 'analyticsData', {
      value: {},
      writable: true,
    });
  });

  it('sets analytics data correctly on mount', () => {
    render(
      <Analytics
        pageName="Company Management: Company Details"
        companyDetails={mockCompanyDetails}
      />
    );

    expect(analytics.update).toHaveBeenCalledWith({
      pageName: 'Company Management: Company Details',
      innBusiness: {
        manageEmployees: 42,
        numOfEmployees: '10-49 Employees',
        profileCompletion: true,
        bookings: 0,
        manageCards: 0,
        numOfStays: 0,
      },
    });
  });

  it('categorizes employees correctly', () => {
    const testCases = [
      { count: 5, expected: '1-9 Employees' },
      { count: 25, expected: '10-49 Employees' },
      { count: 75, expected: '50-99 Employees' },
      { count: 150, expected: '100-249 Employees' },
      { count: 300, expected: '250-499 Employees' },
      { count: 600, expected: '500+ Employees' },
    ];

    testCases.forEach(({ count, expected }) => {
      const companyData = {
        ...mockCompanyDetails,
        requestedCompany: {
          ...mockCompanyDetails.requestedCompany,
          companyDetails: {
            ...mockCompanyDetails.requestedCompany.companyDetails,
            numberOfEmployees: count,
          },
        },
      };

      render(
        <Analytics pageName="Company Management: Company Details" companyDetails={companyData} />
      );

      expect(analytics.update).toHaveBeenCalledWith(
        expect.objectContaining({
          innBusiness: expect.objectContaining({
            numOfEmployees: expected,
          }),
        })
      );
    });
  });

  it('calculates profile completion correctly', () => {
    const incompleteCompanyData = {
      requestedCompany: {
        companyDetails: {
          numberOfEmployees: 10,
          companyName: '',
          companyAddress: {
            addressLine1: '123 Test St',
            postCode: '',
          },
          mainEmployee: {
            emailAddress: 'test@test.com',
            firstName: 'John',
            lastName: '',
          },
        },
      },
    };

    render(
      <Analytics
        pageName="Company Management: Company Details"
        companyDetails={incompleteCompanyData}
      />
    );

    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({
        innBusiness: expect.objectContaining({
          profileCompletion: false,
        }),
      })
    );
  });

  it('preserves existing analytics data', () => {
    Object.defineProperty(window, 'analyticsData', {
      value: {
        existingProperty: 'test',
        innBusiness: {
          stays: 5,
          applications: 3,
        },
      },
      writable: true,
    });

    render(
      <Analytics
        pageName="Company Management: Company Details"
        companyDetails={mockCompanyDetails}
      />
    );

    expect(analytics.update).toHaveBeenCalledWith({
      existingProperty: 'test',
      pageName: 'Company Management: Company Details',
      innBusiness: {
        stays: 5,
        applications: 3,
        manageEmployees: 42,
        numOfEmployees: '10-49 Employees',
        profileCompletion: true,
        bookings: 0,
        manageCards: 0,
        numOfStays: 0,
      },
    });
  });
});
