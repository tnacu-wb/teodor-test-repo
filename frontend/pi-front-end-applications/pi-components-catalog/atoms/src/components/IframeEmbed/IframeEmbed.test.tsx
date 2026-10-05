import IframeEmbed from '.';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';

const mockCallonIframeLoad = jest.fn();

const mockedProps = {
  iframeId: 'paymentFrame',
  iframeContent:
    'CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFuc2xhdGVZKC01MCUpOyB3aWR0aDogMTAwJTsiPgo8ZGl2IHN0eWxlPSJtYXJnaW46IDAgYXV0bzt3aWR0aDogODBweDsiPgogICAgPHN2ZyB3aWR0aD0nODBweCcgaGVpZ2h0PSc4MHB4JyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIgogICAgICAgICBwcmVzZXJ2ZUFzcGVjdFJhdGlvPSJ4TWlkWU1pZCIgY2xhc3M9InVpbC1zcGluIj4KICAgICAgICA8cmVjdCB4PSIwIiB5PSIwIiB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0ibm9uZSIgY2xhc3M9ImJrIj48L3JlY3Q',
  onIframeLoad: mockCallonIframeLoad,
};

describe('IframeEmbed ', () => {
  window.scrollTo = jest.fn();

  afterAll(() => {
    jest.clearAllMocks();
  });

  it('should render a IframeEmbed corectly', function () {
    const { getByTestId } = render(<IframeEmbed {...mockedProps} />);
    expect(getByTestId('paymentContainer')).toBeInTheDocument();
  });

  it('should return null if no iframeContent', function () {
    mockedProps.iframeContent = '';
    const { getByTestId } = render(<IframeEmbed {...mockedProps} />);
    expect(getByTestId('paymentContainer').innerHTML).toBe('');
  });

  it('sets height to full when updateIframeHeight is true', () => {
    const { getByTestId } = render(<IframeEmbed {...mockedProps} updateIframeHeight={true} />);
    const iframeElement = getByTestId('paymentContainer');
    expect(iframeElement).toHaveStyle('height: full');
  });

  it('sets height to auto when updateIframeHeight is false', () => {
    const { getByTestId } = render(<IframeEmbed {...mockedProps} updateIframeHeight={false} />);
    const iframeElement = getByTestId('paymentContainer');
    expect(iframeElement).toHaveStyle('height: auto');
  });
});
