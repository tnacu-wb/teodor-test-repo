import { render, fireEvent, act } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import { BundleExtra, getCorrectBundlePrice } from './BundleExtra.component';

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    analytics: {
      update: jest.fn(),
    },
  };
});

const mockData = {
  bundle: {
    softBundleContent: [
      {
        name: 'Breakfast',
        id: 'BFADBF',
        description: 'Unlimited Premier Inn Breakfast',
        price: 10.99,
        attachments: [
          {
            path: '/',
            label: 'allergy',
          },
        ],
      },
      {
        id: 'FI24HR',
        name: 'Ultimate Wi-fi',
        description: 'Ultimate Wi-fi',
        price: 10.99,
      },
      {
        id: 'HSCKIN',
        description: 'Early Check In',
        price: 10.99,
      },
    ],
    rate: ['FLEXRATE', 'SEMIFLEX'],
    roomClass: ['BIGWIN', 'DBLWIN', 'FMTRPL'],
    isOptional: true,
    key: 'bundle1',
  },
  isActive: false,
  isDisabled: false,
  onClick: jest.fn(),
  testId: 0,
  isV1: true,
  currencySymbol: '\u20AC',
  language: 'en',
  nights: 1,
  adultsNumber: 1,
};

jest.mock('@chakra-ui/react', () => {
  const originalModule = jest.requireActual('@chakra-ui/react');
  const { Flex: OriginalFlex } = originalModule;
  return {
    ...originalModule,
    Flex: (props: any) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { sx, ...rest } = props; // jest fails to use sx correctly
      return <OriginalFlex {...rest} />;
    },
  };
});

describe('BundleExtra', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockData.isDisabled = false;
    mockData.isActive = false;
    window.analyticsData = {};
  });

  it('renders bundle extras button and click', async () => {
    const { getByTestId } = render(<BundleExtra {...mockData} />);
    const bundleButton = getByTestId('Bundle-0');
    expect(bundleButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(bundleButton);
    });
  });

  it('renders bundle extras button and click when disabled', async () => {
    mockData.isDisabled = true;
    const { getByTestId } = render(<BundleExtra {...mockData} currencySymbol={'\u00A3'} />);
    const bundleButton = getByTestId('Bundle-0');
    expect(bundleButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(bundleButton);
    });
  });

  it('renders bundle extras button and click when active', async () => {
    mockData.isActive = true;
    const { getByTestId } = render(<BundleExtra {...mockData} />);
    const bundleButton = getByTestId('Bundle-0');
    expect(bundleButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(bundleButton);
    });
  });

  it('renders bundle extras button and click when active V2', async () => {
    mockData.isActive = true;
    mockData.isV1 = false;
    const { getByTestId } = render(<BundleExtra {...mockData} />);
    const bundleButton = getByTestId('Bundle-0');
    expect(bundleButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(bundleButton);
    });
  });

  it('should strikeThrough a bundle description while comes as true', async () => {
    mockData.isActive = true;
    mockData.isV1 = false;
    const { getAllByTestId } = render(
      <BundleExtra
        {...mockData}
        bundle={{
          ...mockData.bundle,
          softBundleContent: [
            {
              id: 'BFADBF',
              description: 'Breakfast',
              price: 10,
            },
            {
              id: 'FI24HR',
              description: 'Wi-fi',
            },
            {
              id: 'HSCKIN',
              description: 'Early Check In',
              price: 10.99,
              strikeThrough: true,
            },
            {
              id: 'HSCKUO',
              description: 'Late Check Out',
              price: 10.99,
              strikeThrough: true,
            },
          ],
        }}
      />
    );
    const descriptionItems = getAllByTestId('Bundle-Extras-Description-List-Item');
    expect(descriptionItems[2]).toHaveStyle('text-decoration: line-through');
    expect(descriptionItems[3]).toHaveStyle('text-decoration: line-through');
  });

  it('should call analytics update when info icon is clicked', async () => {
    window.analyticsData = { existingData: 'test' } as any;
    const { getAllByTestId } = render(<BundleExtra {...mockData} />);

    // Icon component has data-testid="svg-container", find the one for Info
    const svgContainers = getAllByTestId('svg-container');
    const infoIcon =
      svgContainers.find(
        (container) =>
          container.querySelector('svg')?.getAttribute('data-testid') === 'info-icon' ||
          container.textContent?.includes('Info')
      ) || svgContainers[0];

    await act(async () => {
      fireEvent.click(infoIcon);
    });

    expect(analytics.update).toHaveBeenCalledTimes(2);
    expect(analytics.update).toHaveBeenNthCalledWith(1, {
      bundleSelected: false,
      existingData: 'test',
    });
    expect(analytics.update).toHaveBeenNthCalledWith(2, {
      existingData: 'test',
      bundleInfoSelected: true,
      bundleInfo: (mockData.bundle.softBundleContent ?? []).map((c) => ({
        id: c.id ?? '',
        description: c.description ?? '',
        price: (c.price ?? 0) * (mockData.adultsNumber ?? 1) * (mockData.nights ?? 1),
      })),
    });
  });

  it('should call analytics update when info icon is clicked but no softBundleContent provided', async () => {
    window.analyticsData = { existingData: 'test' } as any;
    const { getAllByTestId } = render(
      <BundleExtra
        {...{ ...mockData, bundle: { ...mockData.bundle, softBundleContent: undefined } }}
      />
    );

    // Icon component has data-testid="svg-container", find the one for Info
    const svgContainers = getAllByTestId('svg-container');
    const infoIcon =
      svgContainers.find(
        (container) =>
          container.querySelector('svg')?.getAttribute('data-testid') === 'info-icon' ||
          container.textContent?.includes('Info')
      ) || svgContainers[0];

    await act(async () => {
      fireEvent.click(infoIcon);
    });

    expect(analytics.update).toHaveBeenCalledTimes(2);
    expect(analytics.update).toHaveBeenNthCalledWith(1, {
      bundleSelected: false,
      existingData: 'test',
    });
    expect(analytics.update).toHaveBeenNthCalledWith(2, {
      existingData: 'test',
      bundleInfoSelected: true,
      bundleInfo: [],
    });
  });

  it('should call analytics update when isActive changes to true', () => {
    mockData.isActive = true;
    render(<BundleExtra {...mockData} />);

    expect(analytics.update).toHaveBeenCalledWith({
      bundleSelected: true,
    });
  });

  it('should call analytics update when isActive changes to false', () => {
    mockData.isActive = false;
    render(<BundleExtra {...mockData} />);

    expect(analytics.update).toHaveBeenCalledWith({
      bundleSelected: false,
    });
  });

  it('should render with Included-Bundle testId when bundle is not optional', () => {
    const includedBundleData = {
      ...mockData,
      bundle: {
        ...mockData.bundle,
        isOptional: false,
      },
    };
    const { getByTestId } = render(<BundleExtra {...includedBundleData} />);
    const includedBundle = getByTestId('Included-Bundle-0');

    expect(includedBundle).toBeInTheDocument();
  });

  it('should render with Bundle testId when bundle is optional', () => {
    const optionalBundleData = {
      ...mockData,
      bundle: {
        ...mockData.bundle,
        isOptional: true,
      },
    };
    const { getByTestId } = render(<BundleExtra {...optionalBundleData} />);
    const optionalBundle = getByTestId('Bundle-0');

    expect(optionalBundle).toBeInTheDocument();
  });

  it('should render descriptions separated by commas', () => {
    const { getAllByTestId } = render(<BundleExtra {...mockData} />);
    const descriptionItems = getAllByTestId('Bundle-Extras-Description-List-Item');

    expect(descriptionItems).toHaveLength(3);
    expect(descriptionItems[0].textContent).toContain('Breakfast, ');
    expect(descriptionItems[1].textContent).toContain('Ultimate Wi-fi, ');
    expect(descriptionItems[2].textContent).not.toContain(', ');
  });

  it('should wrap descriptions with space and parentheses when isClassVariant is true', () => {
    const { getByTestId } = render(<BundleExtra {...mockData} isClassVariant={true} />);
    const descriptionList = getByTestId('Bundle-Extras-Description-List');

    expect(descriptionList.textContent).toContain('(');
    expect(descriptionList.textContent).toContain(')');
    expect(descriptionList.textContent).toMatch(/^\s?\(.*\)$/);
  });

  it('should not wrap descriptions with parentheses when isClassVariant is false', () => {
    const { getByTestId } = render(<BundleExtra {...mockData} isClassVariant={false} />);
    const descriptionList = getByTestId('Bundle-Extras-Description-List');

    expect(descriptionList.textContent).not.toMatch(/^ \(/);
    expect(descriptionList.textContent).not.toMatch(/\)$/);
  });

  it('should render "Add Extras" text only when isClassVariant is true', () => {
    const { queryByTestId, rerender } = render(<BundleExtra {...mockData} isClassVariant={true} />);

    expect(queryByTestId('Bundle-Add-Extras-Text')).toBeInTheDocument();
    expect(queryByTestId('Bundle-Add-Extras-Text')?.textContent).toContain(
      'hoteldetails.rates.bundles.addExtras'
    );

    rerender(<BundleExtra {...mockData} isClassVariant={false} />);

    expect(queryByTestId('Bundle-Add-Extras-Text')).not.toBeInTheDocument();
  });

  it('should calculate packPrice correctly with adultsNumber and nights', () => {
    const testData = {
      ...mockData,
      adultsNumber: 2,
      nights: 3,
    };

    const { getByTestId } = render(<BundleExtra {...testData} />);

    const priceElement = getByTestId('Bundle-Extras-Total-Price');
    const expectedPrice = (10.99 * 2 * 3 + 10.99 * 3 + 10.99).toFixed(2);
    expect(priceElement.textContent).toContain(`€${expectedPrice}`);
  });

  it('should handle missing price values in softBundleContent', () => {
    const testData = {
      ...mockData,
      bundle: {
        ...mockData.bundle,
        softBundleContent: [
          {
            id: 'BFADBF',
            description: 'Breakfast',
            price: 10,
          },
          {
            id: 'FI24HR',
            description: 'Wi-fi',
            price: undefined,
          },
          {
            id: 'HSCKIN',
            description: 'Early Check In',
            price: 5,
          },
        ],
      },
      adultsNumber: 1,
      nights: 1,
    };

    const { getByTestId } = render(<BundleExtra {...testData} />);

    const priceElement = getByTestId('Bundle-Extras-Total-Price');
    expect(priceElement).toHaveTextContent('(+€15.00)');
  });

  it('should calculate analytics bundleInfo prices correctly with adultsNumber and nights', async () => {
    window.analyticsData = {} as any;
    const testData = {
      ...mockData,
      adultsNumber: 2,
      nights: 3,
    };

    const { getAllByTestId } = render(<BundleExtra {...testData} />);

    const svgContainers = getAllByTestId('svg-container');
    const infoIcon = svgContainers[0];

    await act(async () => {
      fireEvent.click(infoIcon);
    });

    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({
        bundleInfoSelected: true,
        bundleInfo: [
          {
            id: 'BFADBF',
            description: 'Unlimited Premier Inn Breakfast',
            price: 10.99 * 2 * 3,
          },
          {
            id: 'FI24HR',
            description: 'Ultimate Wi-fi',
            price: 10.99 * 2 * 3,
          },
          {
            id: 'HSCKIN',
            description: 'Early Check In',
            price: 10.99 * 2 * 3,
          },
        ],
      })
    );
  });

  it('should handle missing price in analytics bundleInfo calculation', async () => {
    window.analyticsData = {} as any;
    const testData = {
      ...mockData,
      bundle: {
        ...mockData.bundle,
        softBundleContent: [
          {
            id: 'BFADBF',
            description: 'Breakfast',
            price: 10,
          },
          {
            id: 'FI24HR',
            description: 'Wi-fi',
          },
        ],
      },
      adultsNumber: 2,
      nights: 2,
    };

    const { getAllByTestId } = render(<BundleExtra {...testData} />);

    const svgContainers = getAllByTestId('svg-container');
    const infoIcon = svgContainers[0];

    await act(async () => {
      fireEvent.click(infoIcon);
    });

    expect(analytics.update).toHaveBeenCalledWith(
      expect.objectContaining({
        bundleInfoSelected: true,
        bundleInfo: [
          {
            id: 'BFADBF',
            description: 'Breakfast',
            price: 10 * 2 * 2,
          },
          {
            id: 'FI24HR',
            description: 'Wi-fi',
            price: 0 * 2 * 2,
          },
        ],
      })
    );
  });
});

describe('getCorrectBundlePrice', () => {
  it('calculates breakfast (BFADBF) price as price * adults * nights', () => {
    const bundle = [
      {
        id: 'BFADBF',
        description: 'Unlimited Premier Inn Breakfast',
        price: 10.99,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 2, 3);
    expect(result).toBe(10.99 * 2 * 3);
  });

  it('calculates breakfast (BBIB) price as price * adults * nights', () => {
    const bundle = [
      {
        id: 'BBIB',
        description: 'Business Breakfast',
        price: 12.99,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 2, 3);
    expect(result).toBe(12.99 * 2 * 3);
  });

  it('calculates wifi (FI24HR) price as price * nights', () => {
    const bundle = [
      {
        id: 'FI24HR',
        description: 'Ultimate Wi-fi',
        price: 5.99,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 2, 3);
    expect(result).toBe(5.99 * 3);
  });

  it('calculates other packages as base price only', () => {
    const bundle = [
      {
        id: 'HSCKIN',
        description: 'Early Check In',
        price: 15.0,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 2, 3);
    expect(result).toBe(15.0);
  });

  it('calculates total for multiple packages with different pricing rules', () => {
    const bundle = [
      {
        id: 'BFADBF',
        description: 'Breakfast',
        price: 10.0,
      },
      {
        id: 'FI24HR',
        description: 'Wi-fi',
        price: 5.0,
      },
      {
        id: 'HSCKIN',
        description: 'Early Check In',
        price: 15.0,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 2, 3);
    const expected = 10.0 * 2 * 3 + 5.0 * 3 + 15.0;
    expect(result).toBe(expected);
  });

  it('handles missing price values as 0', () => {
    const bundle = [
      {
        id: 'BFADBF',
        description: 'Breakfast',
        price: undefined,
      },
      {
        id: 'FI24HR',
        description: 'Wi-fi',
      },
      {
        id: 'HSCKIN',
        description: 'Early Check In',
        price: 10.0,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 2, 3);
    expect(result).toBe(10.0);
  });

  it('returns 0 for empty bundle array', () => {
    const result = getCorrectBundlePrice([], 2, 3);
    expect(result).toBe(0);
  });

  it('calculates correctly with 1 adult and 1 night', () => {
    const bundle = [
      {
        id: 'BFADBF',
        description: 'Breakfast',
        price: 10.0,
      },
      {
        id: 'FI24HR',
        description: 'Wi-fi',
        price: 5.0,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 1, 1);
    expect(result).toBe(10.0 * 1 * 1 + 5.0 * 1);
  });

  it('should not count in strikeThrough prices correctly with 1 adult and 1 night', () => {
    const bundle = [
      {
        id: 'BFADBF',
        description: 'Breakfast',
        price: 10.0,
      },
      {
        id: 'FI24HR',
        description: 'Wi-fi',
        price: 5.0,
      },
      {
        id: 'HSCKIN',
        description: 'Early Check In',
        price: 10.99,
        strikeThrough: true,
      },
      {
        id: 'HSCKUO',
        description: 'Late Check Out',
        price: 10.99,
        strikeThrough: true,
      },
    ];
    const result = getCorrectBundlePrice(bundle, 1, 1);
    expect(result).toBe(10.0 * 1 * 1 + 5.0 * 1);
  });
});
