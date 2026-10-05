import { Box } from '@chakra-ui/react';
import Head from 'next/head';

interface Props {
  children: React.ReactNode;
}

export default function GraphQLPageLayout({ children }: Readonly<Props>) {
  return (
    <Box sx={{ height: '100vh', width: '100vw' }}>
      <Head>
        <title>GraphiQL - NextJS Boilerplate</title>
      </Head>
      {children}
    </Box>
  );
}
