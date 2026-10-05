import '@testing-library/jest-dom';
import { format } from 'date-fns';

import { render } from '../../../utils/test-utils';
import StaticFooter from './StaticFooter.component';

describe('StaticFooter', function () {
  it('Should render StaticFooter', () => {
    const copyrightData = `© ${format(new Date(), 'yyyy')} Premier Inn`;
    const { getByTestId } = render(<StaticFooter />);
    expect(getByTestId('Footer-Wrapper-Error')).toBeInTheDocument();
    expect(getByTestId('Footer-CopyrightSection')).toBeInTheDocument();
    expect(getByTestId('Footer-CopyrightSection').textContent).toEqual(copyrightData);
  });
});
