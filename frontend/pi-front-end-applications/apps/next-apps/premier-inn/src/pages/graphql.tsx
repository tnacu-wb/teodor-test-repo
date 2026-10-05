import dynamic from 'next/dynamic';
import { ReactElement } from 'react';

import { GraphQLPageLayout } from '~components';

// graphiql v1.x is not compatible with React 19 — it accesses
// React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner
// which was removed in React 19. Dynamically importing the entire GraphiQL
// client component with ssr:false prevents the module from being evaluated
// at build time, which would crash the production build even though this
// page is dev-only.
const GraphiQLClient = dynamic(() => import('~components/GraphiQLClient/GraphiQLClient'), {
  ssr: false,
});

export default function GraphQLPage() {
  return <GraphiQLClient />;
}

GraphQLPage.getLayout = function getLayout(page: ReactElement) {
  return <GraphQLPageLayout>{page}</GraphQLPageLayout>;
};

export function getStaticProps() {
  return {
    props: {},
    notFound: process.env.NODE_ENV !== 'development',
  };
}
