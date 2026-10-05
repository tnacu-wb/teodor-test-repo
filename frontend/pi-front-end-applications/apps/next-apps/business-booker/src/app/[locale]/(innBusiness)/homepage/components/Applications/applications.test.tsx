import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { PayApplicationStatus, LOCALES } from '@whitbread-eos/api';

import { Applications, ApplicationsSkeleton, getValidApplications } from './applications';

const mockData = {
  accessLevel: 'SUPER',
};
const applications = [
  {
    applicationGuid: 'guid-1',
    applicationId: 'id-1',
    accountName: 'Test Company 1',
    status: PayApplicationStatus.Submitted,
    scheme: 'GB',
  },
  {
    applicationGuid: 'guid-2',
    applicationId: 'id-2',
    accountName: 'Test Company 2',
    status: PayApplicationStatus.Approved,
    scheme: 'GB',
  },
  {
    applicationGuid: 'guid-3',
    applicationId: 'id-3',
    accountName: 'Test Company 3',
    status: PayApplicationStatus.Started,
    scheme: 'DE',
  },
];
// Mock dependencies
jest.mock('@whitbread-eos/utils/server', () => ({
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
  cn: jest.fn((...args: any[]) => args.join(' ')),
  getTranslations: () => {
    return {
      t: (str: string) => str,
    };
  },
  getUserDetails: () => {
    return {
      companyId: 'test',
      business: {
        accessLevel: mockData.accessLevel,
      },
    };
  },
  getPathForLocale: jest.fn((_locale, path) => `/locale/${path}`),
  ID_TOKEN_COOKIE: 'id_token',
}));
jest.mock('next/headers', () => ({
  cookies: () => ({
    get: () => ({ value: 'mock-token' }),
  }),
}));
jest.mock('next/link', () => {
  const MockLink = ({ children, ...props }: any) => <a {...props}>{children}</a>;
  MockLink.displayName = 'MockNextLink';
  return MockLink;
});
jest.mock('../Analytics/analytics', () => {
  const MockAnalytics = () => <div data-testid="Analytics" />;
  MockAnalytics.displayName = 'MockAnalytics';
  return MockAnalytics;
});
jest.mock('./delete-action', () => {
  const MockDeleteAction = ({ baseDataTestId }: any) => (
    <button data-testid={baseDataTestId}>Delete</button>
  );
  MockDeleteAction.displayName = 'MockDeleteAction';
  return MockDeleteAction;
});

// Silence act warnings for async server components
beforeEach(() => {
  jest.clearAllMocks();
  mockData.accessLevel = 'SUPER';
});

describe('Applications', () => {
  it('renders nothing if no applications', async () => {
    const jsx = await Applications({ locale: LOCALES.EN, applications: [] });
    const { container } = render(jsx as React.ReactElement);
    expect(container.firstChild).toBeNull();
  });

  it('renders applications for lower roles', async () => {
    mockData.accessLevel = 'BOOKER';
    const { queryByTestId } = render(await Applications({ locale: LOCALES.EN, applications }));

    expect(queryByTestId('Applications-AddApplication')).not.toBeInTheDocument();
  });

  it('renders apply for applications for business pay managers', async () => {
    mockData.accessLevel = 'BUSINESS_PAY_MANAGER';
    const { queryByTestId } = render(await Applications({ locale: LOCALES.EN, applications }));
    expect(queryByTestId('Applications-AddApplication')).toBeInTheDocument();
  });

  it('renders applications and actions', async () => {
    const { findByTestId, getByTestId } = render(
      await Applications({ locale: LOCALES.EN, applications })
    );

    expect(await findByTestId('Applications-Container')).toBeInTheDocument();
    expect(getByTestId('Applications-Title')).toHaveTextContent(
      'homepage.home.innbusinessPay.applications.heading'
    );
    // Company names
    expect(getByTestId('Applications-ListItemCompanyName-guid-1')).toHaveTextContent(
      'Test Company 1'
    );
    expect(getByTestId('Applications-ListItemCompanyName-guid-2')).toHaveTextContent(
      'Test Company 2'
    );
    expect(getByTestId('Applications-ListItemCompanyName-guid-3')).toHaveTextContent(
      'Test Company 3'
    );
    // Statuses
    expect(getByTestId('Applications-ListItemStatus-guid-1')).toHaveTextContent(
      'homepage.home.innbusinessPay.applications.submitted'
    );
    expect(getByTestId('Applications-ListItemStatus-guid-2')).toHaveTextContent(
      'homepage.home.innbusinessPay.applications.approved'
    );
    expect(getByTestId('Applications-ListItemStatus-guid-3')).toHaveTextContent(
      'homepage.home.innbusinessPay.applications.started'
    );

    expect(getByTestId('Applications-ListItemResume-guid-3')).toBeInTheDocument();
    expect(getByTestId('Applications-ListItemDelete-guid-3')).toBeInTheDocument();
    expect(getByTestId('Applications-ListItemRegister-guid-2')).toBeInTheDocument();
    expect(screen.getByTestId('Applications-ListItemInReview-guid-1')).toBeInTheDocument();
    // Add Application button
    expect(getByTestId('Applications-AddApplication')).toBeInTheDocument();
    // Analytics
    expect(getByTestId('Analytics')).toBeInTheDocument();
  });

  it('does not render Add Application button if showApply is false', async () => {
    const { queryByTestId } = render(
      await Applications({ locale: LOCALES.EN, showApply: false, applications })
    );
    expect(queryByTestId('Applications-AddApplication')).not.toBeInTheDocument();
  });

  it('renders correctly when showApply is true', async () => {
    const { getByTestId } = render(
      await Applications({ locale: LOCALES.EN, showApply: true, applications })
    );
    expect(getByTestId('Applications-AddApplication')).toBeInTheDocument();
  });

  it('renders correctly for different locales', async () => {
    const { getByTestId } = render(await Applications({ locale: LOCALES.DE, applications }));
    expect(getByTestId('Applications-Container')).toBeInTheDocument();
  });

  it('renders nothing if applications is empty', async () => {
    const jsx = await Applications({ locale: LOCALES.EN, applications: [] });
    const { container } = render(jsx as React.ReactElement);
    expect(container.firstChild).toBeNull();
  });

  it('renders Approved application with register link', async () => {
    const approvedApplication = [
      {
        applicationGuid: 'guid-approved',
        applicationId: 'id-approved',
        accountName: 'Approved Company',
        status: PayApplicationStatus.Approved,
        scheme: 'GB',
      },
    ];
    const { getByTestId, queryByTestId } = render(
      await Applications({ locale: LOCALES.EN, applications: approvedApplication })
    );
    expect(getByTestId('Applications-ListItemCompanyName-guid-approved')).toHaveTextContent(
      'Approved Company'
    );
    expect(getByTestId('Applications-ListItemStatus-guid-approved')).toHaveTextContent(
      'homepage.home.innbusinessPay.applications.approved'
    );
    expect(getByTestId('Applications-ListItemRegister-guid-approved')).toBeInTheDocument();
    expect(queryByTestId('Applications-ListItemDelete-guid-approved')).not.toBeInTheDocument();
    expect(queryByTestId('Applications-ListItemResume-guid-approved')).not.toBeInTheDocument();
  });

  it('renders Submitted application with in review text', async () => {
    const submittedApplication = [
      {
        applicationGuid: 'guid-submitted',
        applicationId: 'id-submitted',
        accountName: 'Submitted Company',
        status: PayApplicationStatus.Submitted,
        scheme: 'GB',
      },
    ];
    const { getByTestId, queryByTestId } = render(
      await Applications({ locale: LOCALES.EN, applications: submittedApplication })
    );
    expect(getByTestId('Applications-ListItemCompanyName-guid-submitted')).toHaveTextContent(
      'Submitted Company'
    );
    expect(getByTestId('Applications-ListItemStatus-guid-submitted')).toHaveTextContent(
      'homepage.home.innbusinessPay.applications.submitted'
    );
    expect(screen.getByTestId('Applications-ListItemInReview-guid-submitted')).toBeInTheDocument();
    expect(queryByTestId('Applications-ListItemResume-guid-submitted')).not.toBeInTheDocument();
    expect(queryByTestId('Applications-ListItemDelete-guid-submitted')).not.toBeInTheDocument();
  });

  it('uses correct locale for resume link', async () => {
    const resumeApplications = [
      {
        applicationGuid: 'guid-gb',
        applicationId: 'id-gb',
        accountName: 'GB Company',
        status: PayApplicationStatus.Started,
        scheme: 'GB',
      },
      {
        applicationGuid: 'guid-de',
        applicationId: 'id-de',
        accountName: 'DE Company',
        status: PayApplicationStatus.Started,
        scheme: 'DE',
      },
    ];
    const { getByTestId } = render(
      await Applications({ locale: LOCALES.EN, applications: resumeApplications })
    );
    // GB scheme uses EN locale
    expect(getByTestId('Applications-ListItemResume-guid-gb').getAttribute('href')).toContain(
      '/locale/business-pay/pay-application-resume?applicationId=id-gb&applicationGuid=guid-gb'
    );
    // DE scheme uses DE locale
    expect(getByTestId('Applications-ListItemResume-guid-de').getAttribute('href')).toContain(
      '/locale/business-pay/pay-application-resume?applicationId=id-de&applicationGuid=guid-de'
    );
  });

  it('renders with missing applicationGuid', async () => {
    const applicationWithMissingGuid = [
      {
        applicationId: 'id-no-guid',
        accountName: 'No Guid Company',
        status: PayApplicationStatus.Started,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(
      await Applications({ locale: LOCALES.EN, applications: applicationWithMissingGuid })
    );
    expect(getByTestId('Applications-ListItemCompanyName-0')).toHaveTextContent('No Guid Company');
    expect(getByTestId('Applications-ListItemResume-0')).toBeInTheDocument();
    expect(getByTestId('Applications-ListItemDelete-0')).toBeInTheDocument();
  });

  it('renders with missing accountName', async () => {
    const applicationWithMissingAccountName = [
      {
        applicationGuid: 'guid-no-name',
        applicationId: 'id-no-name',
        status: PayApplicationStatus.Started,
        scheme: 'GB',
      },
    ];
    const { getByTestId } = render(
      await Applications({ locale: LOCALES.EN, applications: applicationWithMissingAccountName })
    );
    expect(getByTestId('Applications-ListItemCompanyName-guid-no-name')).toHaveTextContent('');
  });
});

describe('ApplicationsSkeleton', () => {
  it('renders skeleton UI', () => {
    const t = (key: string) => key;
    render(<ApplicationsSkeleton t={t} />);
    expect(screen.getByTestId('Applications-Skeleton')).toBeInTheDocument();
    expect(
      screen.getByText('homepage.home.innbusinessPay.applications.heading')
    ).toBeInTheDocument();
    const skeletons = document.getElementsByClassName('animate-pulse');
    expect(skeletons.length).toBe(4);
  });

  it('renders correct number of skeleton rows', () => {
    const t = (key: string) => key;
    render(<ApplicationsSkeleton t={t} />);
    const skeletons = document.getElementsByClassName('animate-pulse');
    expect(skeletons).toHaveLength(4);
  });

  it('applies correct classes to skeleton elements', () => {
    const t = (key: string) => key;
    render(<ApplicationsSkeleton t={t} />);
    const skeletons = document.getElementsByClassName('animate-pulse');
    expect(skeletons[0]).toHaveClass('h-12', 'md:h-6', 'w-full', 'mb-4', 'mt-4');
    expect(skeletons[1]).toHaveClass('h-12', 'md:h-6', 'w-full', 'mb-4');
    expect(skeletons[2]).toHaveClass('h-12', 'md:h-6', 'w-full', 'mb-4');
    expect(skeletons[3]).toHaveClass('h-[40px]', 'w-full', 'md:w-1/5', 'mt-4');
  });

  it('renders heading with correct test id and class', () => {
    const t = (key: string) => key;
    render(<ApplicationsSkeleton t={t} />);
    const heading = screen.getByText('homepage.home.innbusinessPay.applications.heading');
    expect(heading).toBeInTheDocument();
    expect(heading).toHaveClass('text-[1.44rem]', 'leading-[2rem]', 'font-bold', 'mb-2');
  });
});

describe('getValidApplications', () => {
  it('should filter and return only applications with valid statuses', () => {
    const mixedApplications = [
      {
        applicationGuid: 'guid-1',
        applicationId: 'id-1',
        accountName: 'Approved App',
        status: PayApplicationStatus.Approved,
        scheme: 'GB',
      },
      {
        applicationGuid: 'guid-2',
        applicationId: 'id-2',
        accountName: 'Started App',
        status: PayApplicationStatus.Started,
        scheme: 'GB',
      },
      {
        applicationGuid: 'guid-3',
        applicationId: 'id-3',
        accountName: 'Submitted App',
        status: PayApplicationStatus.Submitted,
        scheme: 'GB',
      },
      {
        applicationGuid: 'guid-4',
        applicationId: 'id-4',
        accountName: 'Rejected App',
        status: PayApplicationStatus.Rejected,
        scheme: 'GB',
      },
      {
        applicationGuid: 'guid-5',
        applicationId: 'id-5',
        accountName: 'Cancelled App',
        status: PayApplicationStatus.Cancelled,
        scheme: 'GB',
      },
    ];

    const result = getValidApplications(mixedApplications);

    expect(result).toHaveLength(3);
    expect(result[0].status).toBe(PayApplicationStatus.Approved);
    expect(result[1].status).toBe(PayApplicationStatus.Started);
    expect(result[2].status).toBe(PayApplicationStatus.Submitted);
    expect(result.every((app) => app.status !== PayApplicationStatus.Rejected)).toBe(true);
    expect(result.every((app) => app.status !== PayApplicationStatus.Cancelled)).toBe(true);
  });

  it('should return empty array when input is undefined', () => {
    const result = getValidApplications(undefined);

    expect(result).toEqual([]);
  });

  it('should return empty array when all applications have invalid statuses', () => {
    const invalidApplications = [
      {
        applicationGuid: 'guid-1',
        applicationId: 'id-1',
        accountName: 'Rejected App',
        status: PayApplicationStatus.Rejected,
        scheme: 'GB',
      },
      {
        applicationGuid: 'guid-2',
        applicationId: 'id-2',
        accountName: 'Cancelled App',
        status: PayApplicationStatus.Cancelled,
        scheme: 'GB',
      },
    ];

    const result = getValidApplications(invalidApplications);

    expect(result).toEqual([]);
  });
});
