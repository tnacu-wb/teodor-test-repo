import { QueryClient } from '@tanstack/react-query';
import { GetServerSidePropsContext } from 'next';

export interface InitialApplicationDataLoaderFnProps extends GetServerSidePropsContext {
  queryClient: QueryClient;
}
