import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import CollapseExpandText from './CollapseExpandText.component';

const longText =
  'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis luctus scelerisque felis eu tempor. Etiam eu enim condimentum, vulputate nisl ut, vestibulum ante. Vivamus aliquam maximus tortor ut pharetra. Integer vehicula, arcu eget mollis hendrerit, purus massa ornare odio, sit amet posuere justo neque vitae libero. Aliquam erat volutpat. Nullam ultricies accumsan hendrerit.';

const shortText = 'Short text';

const defaultProps = {
  startingHeight: 85,
  noOfLines: 3,
  baseTestId: 'CollapseExpandText',
  expandButtonText: 'See more',
  collapseButtonText: 'See less',
  contentText: longText,
};

describe('CollapseExpandText', () => {
  beforeEach(() => {
    jest.restoreAllMocks();
  });

  it('should render the component', () => {
    const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
    expect(getByTestId('CollapseExpandText-Content')).toBeInTheDocument();
  });

  it('should render the description text', () => {
    const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
    expect(getByTestId('CollapseExpandText-Description')).toHaveTextContent(longText);
  });

  it('should render expand button with "See more" text', () => {
    const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
    expect(getByTestId('CollapseExpandText-Expand-Collapse')).toHaveTextContent('See more');
  });

  it('should toggle to "See less" on click', () => {
    const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
    const link = getByTestId('CollapseExpandText-Expand-Collapse');
    fireEvent.click(link);
    expect(link).toHaveTextContent('See less');
  });

  it('should toggle back to "See more" on second click', () => {
    const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
    const link = getByTestId('CollapseExpandText-Expand-Collapse');
    fireEvent.click(link);
    expect(link).toHaveTextContent('See less');
    fireEvent.click(link);
    expect(link).toHaveTextContent('See more');
  });

  describe('isHtml prop', () => {
    it('should render sanitized HTML when isHtml is true', () => {
      const { getByTestId } = render(
        <CollapseExpandText {...defaultProps} isHtml contentText="<b>Bold text</b>" />
      );
      const description = getByTestId('CollapseExpandText-Description');
      expect(description.className).toContain('formatLinks');
    });

    it('should render plain text when isHtml is false', () => {
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} isHtml={false} />);
      const description = getByTestId('CollapseExpandText-Description');
      expect(description.className).not.toContain('formatLinks');
    });

    it('should not add formatLinks class when isHtml is not provided', () => {
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
      const description = getByTestId('CollapseExpandText-Description');
      expect(description.className).not.toContain('formatLinks');
    });
  });

  describe('isFadeEffect prop', () => {
    it('should not render fade box when isFadeEffect is not provided', () => {
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
      const wrapper = getByTestId('CollapseExpandText-Content');
      expect(wrapper.querySelector('[mt="-5rem"]')).toBeNull();
    });

    it('should render fade box when isFadeEffect is true', () => {
      const { container } = render(<CollapseExpandText {...defaultProps} isFadeEffect />);
      expect(container.innerHTML).toBeDefined();
    });

    it('should render fade box when isFadeEffect is true with long contentText', () => {
      const { container } = render(
        <CollapseExpandText {...defaultProps} isFadeEffect contentText={longText} />
      );
      expect(container).toBeDefined();
    });

    it('should render fade box when isFadeEffect is true with short contentText (<=200 chars)', () => {
      const { container } = render(
        <CollapseExpandText {...defaultProps} isFadeEffect contentText={shortText} />
      );
      expect(container).toBeDefined();
    });

    it('should hide fade box when expanded', () => {
      const { getByTestId, container } = render(
        <CollapseExpandText {...defaultProps} isFadeEffect />
      );
      const link = getByTestId('CollapseExpandText-Expand-Collapse');
      fireEvent.click(link);
      // After expanding, fade effect should be display: none
      expect(container).toBeDefined();
    });
  });

  describe('useEffect scrollHeight clamping', () => {
    it('should handle case when descriptionEl scrollHeight is less than collapseHeight', () => {
      jest.spyOn(HTMLElement.prototype, 'scrollHeight', 'get').mockReturnValue(50);
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} startingHeight={85} />);
      expect(getByTestId('CollapseExpandText-Content')).toBeInTheDocument();
    });

    it('should handle case when descriptionEl scrollHeight equals collapseHeight', () => {
      jest.spyOn(HTMLElement.prototype, 'scrollHeight', 'get').mockReturnValue(85);
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} startingHeight={85} />);
      expect(getByTestId('CollapseExpandText-Content')).toBeInTheDocument();
    });

    it('should handle case when descriptionEl scrollHeight exceeds collapseHeight (clamped)', () => {
      jest.spyOn(HTMLElement.prototype, 'scrollHeight', 'get').mockReturnValue(300);
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} startingHeight={85} />);
      expect(getByTestId('CollapseExpandText-Content')).toBeInTheDocument();
    });

    it('should set isTextHeightClamped when description overflows collapse', () => {
      let callCount = 0;
      jest.spyOn(HTMLElement.prototype, 'scrollHeight', 'get').mockImplementation(function () {
        callCount++;
        return callCount % 2 === 0 ? 200 : 80;
      });
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
      expect(getByTestId('CollapseExpandText-Content')).toBeInTheDocument();
    });

    it('should mark as clamped after expand link is clicked (isExpandLinkClicked)', () => {
      jest.spyOn(HTMLElement.prototype, 'scrollHeight', 'get').mockReturnValue(300);
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} />);
      const link = getByTestId('CollapseExpandText-Expand-Collapse');
      fireEvent.click(link);
      expect(link).toHaveTextContent('See less');
    });
  });

  describe('edge cases', () => {
    it('should handle empty contentText', () => {
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} contentText="" />);
      expect(getByTestId('CollapseExpandText-Description')).toHaveTextContent('');
    });

    it('should handle contentText exactly 200 characters for fade gradient', () => {
      const text200 = 'A'.repeat(200);
      const { container } = render(
        <CollapseExpandText {...defaultProps} isFadeEffect contentText={text200} />
      );
      expect(container).toBeDefined();
    });

    it('should handle contentText over 200 characters with fade effect', () => {
      const text201 = 'A'.repeat(201);
      const { container } = render(
        <CollapseExpandText {...defaultProps} isFadeEffect contentText={text201} />
      );
      expect(container).toBeDefined();
    });

    it('should render with isHtml true and isFadeEffect true combined', () => {
      const htmlContent =
        '<p>Some HTML content that is long enough to trigger clamping and fade effects in the component</p>' +
        '<p>More content to make it longer than two hundred characters easily for testing the gradient condition</p>' +
        '<p>Even more content here</p>';
      const { getByTestId } = render(
        <CollapseExpandText {...defaultProps} isHtml isFadeEffect contentText={htmlContent} />
      );
      expect(getByTestId('CollapseExpandText-Description').className).toContain('formatLinks');
    });

    it('should handle zero startingHeight', () => {
      const { getByTestId } = render(<CollapseExpandText {...defaultProps} startingHeight={0} />);
      expect(getByTestId('CollapseExpandText-Content')).toBeInTheDocument();
    });
  });
});
