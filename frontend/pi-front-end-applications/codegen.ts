import { CodegenConfig } from '@graphql-codegen/cli';
import * as dotenv from 'dotenv';

const config: CodegenConfig = {
  overwrite: true,
  generates: {
    'pi-components-catalog/api/src/types/graphql.ts': {
      plugins: ['typescript', 'typescript-document-nodes'],
      config: {
        skipTypename: true,
        maybeValue: 'T',
      },
    },
  },
};

if (process.argv.includes('--local')) {
  dotenv.config({ path: 'apps/next-apps/premier-inn/.env.local' });

  config.schema = process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT;
} else {
  const targetEnv = process.argv
    .find((url) => url.startsWith('--targetEnv='))
    ?.replace('--targetEnv=', '');
  config.schema = `https://api.${targetEnv}.premierinn.digital/graphql`;
}

export default config;
