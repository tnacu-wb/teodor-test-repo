import { Box } from '@chakra-ui/react';
import { PI_FAVICON } from '@whitbread-eos/api';
import { formatAssetsUrl } from '@whitbread-eos/utils';
import Head from 'next/head';

interface Props {
  children: React.ReactNode;
}

export default function GraphQLPageLayout({ children }: Readonly<Props>) {
  return (
    <Box sx={{ height: '100vh', width: '100vw' }}>
      <Head>
        <title>GraphiQL - NextJS Boilerplate</title>
        <link rel="icon" type="image/x-icon" href={formatAssetsUrl(PI_FAVICON)} />
      </Head>
      {children}
    </Box>
  );
}
