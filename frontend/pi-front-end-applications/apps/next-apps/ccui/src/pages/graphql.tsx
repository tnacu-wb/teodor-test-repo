import 'graphiql/graphiql.css';
import type {
  GraphQLArgument,
  GraphQLEnumType,
  GraphQLField,
  GraphQLInputField,
  GraphQLOutputType,
  GraphQLScalarType,
  GraphQLSchema,
  ValueNode,
} from 'graphql';
import {
  buildClientSchema,
  DefinitionNode,
  getIntrospectionQuery,
  isEnumType,
  isWrappingType,
  Kind,
  parse,
} from 'graphql';
import fetch from 'isomorphic-unfetch';
import dynamic from 'next/dynamic';
import { ReactElement, useEffect, useState } from 'react';

import { GraphQLPageLayout } from '~components';

// Load GraphiQL components only on client side to avoid React 19 SSR issues
const GraphiQL = dynamic(() => import('graphiql'), { ssr: false }) as any;
const GraphiQLExplorer = dynamic(() => import('graphiql-explorer'), { ssr: false }) as any;

let graphiQLLegacyRef: any;

const DEFAULT_QUERY = `
# Welcome to GraphiQL
#
# GraphiQL is an in-browser tool for writing, validating, and
# testing GraphQL queries.
#
# Type queries into this side of the screen, and you will see intelligent
# typeaheads aware of the current GraphQL type schema and live syntax and
# validation errors highlighted within the text.
#
# GraphQL queries typically start with a "{" character. Lines that start
# with a # are ignored.
#
# An example GraphQL query might look like:
#
#     {
#       field(arg: "value") {
#         subField
#       }
#     }
#
# Keyboard shortcuts:
#
#  Prettify Query:  Shift-Ctrl-P (or press the prettify button above)
#
#       Run Query:  Ctrl-Enter (or press the play button above)
#
#   Auto Complete:  Ctrl-Space (or just start typing)
#
# Explorer:
#
#   - shift-option/alt-click on the query below to jump to it in the explorer
#   - option/alt-click on a field in the explorer to select all subfields
#
# NOTE:
#
# To see all available queries and mutations, select "Query" or "Mutation" in the "Add new" dropdown
# located on the lower left side of the screen and click the + sign.
`;

export default function GraphQLPage() {
  const [schema, setSchema] = useState<GraphQLSchema>();
  const [query, setQuery] = useState(DEFAULT_QUERY);
  const [isExplorereOpen, setIsExplorereOpen] = useState(true);
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    if (!isMounted) return;

    fetchSchema().catch((e) => {
      console.log(e);
    });

    setTimeout(() => {
      const editor = graphiQLLegacyRef.getQueryEditor();
      editor?.setOption('extraKeys', {
        ...(editor.options.extraKeys || {}),
        'Shift-Alt-LeftClick': handleInspectOperation,
      });
    }, 0);

    async function fetchSchema() {
      const response = await fetcher({
        query: getIntrospectionQuery(),
      });
      setSchema(buildClientSchema(response.data));
    }

    function handleInspectOperation(cm: any, mousePos: { line: number; ch: number }) {
      const parsedQuery = parse(query || '');

      if (!parsedQuery) {
        console.error("Couldn't parse query document");
        return null;
      }

      const token = cm.getTokenAt(mousePos);
      const start = { line: mousePos.line, ch: token.start };
      const end = { line: mousePos.line, ch: token.end };
      const relevantMousePos = {
        start: cm.indexFromPos(start),
        end: cm.indexFromPos(end),
      };

      const position = relevantMousePos;

      const def = parsedQuery.definitions.find((definition) => {
        if (!definition.loc) {
          console.log('Missing location information for definition');
          return false;
        }

        const { start, end } = definition.loc;
        return start <= position.start && end >= position.end;
      });

      if (!def) {
        console.error('Unable to find definition corresponding to mouse position');
        return null;
      }

      const operationKind = getOperationKind(def);

      const operationName = getOperationName(def);

      const selector = `.graphiql-explorer-root #${operationKind}-${operationName}`;

      const el = document.querySelector(selector);
      el?.scrollIntoView();
    }
  }, [query]);

  function getOperationKind(def: DefinitionNode) {
    const fragment = def.kind === 'FragmentDefinition' ? 'fragment' : 'unknown';
    return def.kind === 'OperationDefinition' ? def.operation : fragment;
  }

  function getOperationName(def: DefinitionNode) {
    const fragment = def.kind === 'FragmentDefinition' && !!def.name ? def.name.value : 'unknown';
    return def.kind === 'OperationDefinition' && !!def.name ? def.name.value : fragment;
  }

  if (!isMounted) {
    return <div className="container graphiql-container">Loading GraphiQL...</div>;
  }

  return (
    <div className="container graphiql-container">
      <GraphiQLExplorer
        fetcher={fetcher}
        schema={schema}
        query={query}
        onEdit={(input: string) => setQuery(input)}
        onRunOperation={(operationName: any) => graphiQLLegacyRef.handleRunQuery(operationName)}
        explorerIsOpen={isExplorereOpen}
        onToggleExplorer={() => setIsExplorereOpen(!isExplorereOpen)}
        getDefaultScalarArgValue={getDefaultScalarArgValue}
        makeDefaultArg={makeDefaultArg}
      />
      <GraphiQL
        fetcher={fetcher}
        schema={schema}
        query={query}
        ref={(ref: any) => (graphiQLLegacyRef = ref)}
      >
        <GraphiQL.Toolbar>
          <GraphiQL.Button
            onClick={() => graphiQLLegacyRef.handlePrettifyQuery()}
            label="Prettify"
            title="Prettify Query (Shift-Ctrl-P)"
          />
          <GraphiQL.Button
            onClick={() => graphiQLLegacyRef.handleToggleHistory()}
            label="History"
            title="Show History"
          />
          <GraphiQL.Button
            onClick={() => setIsExplorereOpen(!isExplorereOpen)}
            label="Explorer"
            title="Toggle Explorer"
          />
        </GraphiQL.Toolbar>
      </GraphiQL>
    </div>
  );
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

async function fetcher(graphQLParams: any) {
  const endpoint =
    process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT === 'true' &&
    typeof window === 'undefined'
      ? process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT
      : process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT;

  const response = await fetch(endpoint!, {
    method: 'post',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(graphQLParams),
  });
  const result = await response.json();

  return result;
}

function makeDefaultArg(
  parentField: GraphQLField<any, any>,
  arg: GraphQLArgument | GraphQLInputField
): boolean {
  const unwrappedType = unwrapOutputType(parentField.type);
  if (
    unwrappedType.name.startsWith('GitHub') &&
    unwrappedType.name.endsWith('Connection') &&
    (arg.name === 'first' || arg.name === 'orderBy')
  ) {
    return true;
  }
  return false;
}

function unwrapOutputType(outputType: GraphQLOutputType) {
  let unwrappedType = outputType;
  while (isWrappingType(unwrappedType)) {
    unwrappedType = unwrappedType.ofType;
  }
  return unwrappedType;
}

function getDefaultScalarArgValue(
  parentField: GraphQLField<any, any>,
  arg: GraphQLArgument | GraphQLInputField,
  argType: GraphQLEnumType | GraphQLScalarType
): ValueNode {
  const unwrappedType = unwrapOutputType(parentField.type);
  switch (unwrappedType.name) {
    case 'GitHubRepository':
      if (arg.name === 'name') {
        return { kind: 'StringValue' as Kind.STRING, value: 'graphql-js' };
      } else if (arg.name === 'owner') {
        return { kind: 'StringValue' as Kind.STRING, value: 'graphql' };
      }
      break;
    case 'NpmPackage':
      if (arg.name === 'name') {
        return { kind: 'StringValue' as Kind.STRING, value: 'graphql' };
      }
      break;
    default:
      if (
        isEnumType(argType) &&
        unwrappedType.name.startsWith('GitHub') &&
        unwrappedType.name.endsWith('Connection')
      ) {
        if (
          arg.name === 'direction' &&
          argType
            .getValues()
            .map((x) => x.name)
            .includes('DESC')
        ) {
          return { kind: 'EnumValue' as Kind.STRING, value: 'DESC' };
        } else if (
          arg.name === 'field' &&
          argType
            .getValues()
            .map((x) => x.name)
            .includes('CREATED_AT')
        ) {
          return { kind: 'EnumValue' as Kind.STRING, value: 'CREATED_AT' };
        }
      }
      return GraphiQLExplorer.defaultValue(argType);
  }
  return GraphiQLExplorer.defaultValue(argType);
}
