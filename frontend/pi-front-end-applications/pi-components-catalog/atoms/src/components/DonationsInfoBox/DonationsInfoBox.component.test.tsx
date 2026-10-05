import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import DonationsInfoBox from './DonationsInfoBox.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

const donationsInfoBoxText =
  "<p>We’ve all seen the humanitarian crisis affecting the people of the Ukraine. Our hearts are with all those whose lives have been affected by these devastating events, and the unimaginable suffering caused by this needless human tragedy. 100% of your donation will go to The Disasters Emergency Committee (DEC) in support of their international humanitarian aid effort. It's the task of this charity to supply food, water, first aid, medicine, warm clothes, and shelter for refugees, which is why any support you can offer today will be invaluable in helping the people of Ukraine.*</p>";

describe('Donations', () => {
  it('should render DonationInfoBox corectly', function () {
    const { getByTestId } = render(<DonationsInfoBox informationBox={donationsInfoBoxText} />);

    expect(getByTestId('Donation-InfoBox')).toBeInTheDocument();
    expect(getByTestId('Donation-InfoBoxIcon')).toBeInTheDocument();
    expect(getByTestId('Donation-InfoBoxText')).toBeInTheDocument();
    expect(getByTestId('Donation-InfoBoxText')).toHaveTextContent(
      donationsInfoBoxText.replace(/<\/?[^>]+(>|$)/g, '')
    );
  });
});
