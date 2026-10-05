import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import ExpandText from './ExpandText.component';

describe('ExpandText', () => {
  it('should render the component', () => {
    const { getByText } = render(<ExpandText>text</ExpandText>);
    const text = getByText(/text/i);
    expect(text).toBeInTheDocument();
  });
  it('should have the predefined height from props', () => {
    const { getByTestId } = render(
      <ExpandText startingHeight={87}>
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis luctus scelerisque felis eu
        tempor. Etiam eu enim condimentum, vulputate nisl ut, vestibulum ante. Vivamus aliquam
        maximus tortor ut pharetra. Integer vehicula, arcu eget mollis hendrerit, purus massa ornare
        odio, sit amet posuere justo neque vitae libero. Aliquam erat volutpat. Nullam ultricies
        accumsan hendrerit. Praesent in purus arcu. Praesent placerat, libero et congue viverra,
        augue leo lobortis sapien, vitae cursus tellus enim facilisis purus. Pellentesque aliquet,
        velit sit amet malesuada euismod, diam ante ultricies est, eget vulputate sapien tellus eu
        magna. Aenean porttitor lorem ut posuere luctus. Nulla vitae consectetur quam. Sed viverra
        magna eget tortor gravida placerat. Donec ac pretium nunc. Lorem ipsum dolor sit amet,
        consectetur adipiscing elit. Aenean non urna vel lorem tristique vulputate. Aenean tortor
        sem, fringilla vel ante non, suscipit viverra augue. In pharetra risus at blandit dignissim.
        Morbi et lacus libero. Sed a urna id sem porttitor rutrum eu et dolor. Donec eget libero
        volutpat, varius enim eget, auctor diam. In et tortor eu mauris ornare lobortis non id
        tellus. Phasellus laoreet massa et odio rhoncus facilisis. Donec faucibus felis augue, non
        rhoncus magna tincidunt ac.
      </ExpandText>
    );

    const wrapper = getByTestId('wrapper');
    expect(wrapper).toHaveStyle('height: 87px');
  });
  it('should have all the text prerendered even if it is not properly shown', () => {
    const { getByText } = render(
      <ExpandText startingHeight={87}>
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis luctus scelerisque felis eu
        tempor. Etiam eu enim condimentum, vulputate nisl ut, vestibulum ante. Vivamus aliquam
        maximus tortor ut pharetra. Integer vehicula, arcu eget mollis hendrerit, purus massa ornare
        odio, sit amet posuere justo neque vitae libero. Aliquam erat volutpat. Nullam ultricies
        accumsan hendrerit. Praesent in purus arcu. Praesent placerat, libero et congue viverra,
        augue leo lobortis sapien, vitae cursus tellus enim facilisis purus. Pellentesque aliquet,
        velit sit amet malesuada euismod, diam ante ultricies est, eget vulputate sapien tellus eu
        magna. Aenean porttitor lorem ut posuere luctus. Nulla vitae consectetur quam. Sed viverra
        magna eget tortor gravida placerat. Donec ac pretium nunc. Lorem ipsum dolor sit amet,
        consectetur adipiscing elit. Aenean non urna vel lorem tristique vulputate. Aenean tortor
        sem, fringilla vel ante non, suscipit viverra augue. In pharetra risus at blandit dignissim.
        Morbi et lacus libero. Sed a urna id sem porttitor rutrum eu et dolor. Donec eget libero
        volutpat, varius enim eget, auctor diam. In et tortor eu mauris ornare lobortis non id
        tellus. Phasellus laoreet massa et odio rhoncus facilisis. Donec faucibus felis augue, non
        rhoncus magna tincidunt ac.
      </ExpandText>
    );

    const text = getByText(/lorem ipsum/i);
    expect(text).toBeInTheDocument();
  });
});
