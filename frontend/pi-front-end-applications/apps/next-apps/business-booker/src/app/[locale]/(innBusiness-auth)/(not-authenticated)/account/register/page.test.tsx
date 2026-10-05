import { CompanyType } from '@whitbread-eos/api';

import { generateMetadata } from './page';

describe('generateMetadata', () => {
  it('should return robots noindex for BUSINESS_PAY type', async () => {
    const searchParams = Promise.resolve({ type: CompanyType.BUSINESS_PAY });
    const result = await generateMetadata({ searchParams });
    expect(result).toEqual({
      robots: {
        index: false,
        follow: false,
      },
    });
  });

  it('should return robots index for other types', async () => {
    const searchParams = Promise.resolve({ type: 'OTHER_TYPE' });
    const result = await generateMetadata({ searchParams });
    expect(result).toEqual({
      robots: {
        index: true,
        follow: true,
      },
    });
  });
});
