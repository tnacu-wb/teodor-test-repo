import GraphQLPageLayout from '.';

import { render } from '~utils/test-utils';

describe('GraphQLPageLayout', () => {
  it('should render a <GraphQLPageLayout> with children', function () {
    const { getByText } = render(
      <GraphQLPageLayout>
        <p>test</p>
      </GraphQLPageLayout>,
      {}
    );

    getByText('test');
  });
});
