import GraphiQLExplorer from 'graphiql-explorer';
import 'graphiql/graphiql.css';
import type {
  GraphQLArgument,
  GraphQLEnumType,
  GraphQLField,
  GraphQLInputField,
  GraphQLOutputType,
  GraphQLScalarType,
  ValueNode,
} from 'graphql';
import { isEnumType, isWrappingType, Kind } from 'graphql';
import fetch from 'isomorphic-unfetch';

export async function fetcher(graphQLParams: any) {
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

export function makeDefaultArg(
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

export function unwrapOutputType(outputType: GraphQLOutputType) {
  let unwrappedType = outputType;
  while (isWrappingType(unwrappedType)) {
    unwrappedType = unwrappedType.ofType;
  }
  return unwrappedType;
}

export function getDefaultScalarArgValue(
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

export function getOperationKind(def: any) {
  const operationDefKind = def.kind === 'FragmentDefinition' ? 'fragment' : 'unknown';
  return def.kind === 'OperationDefinition' ? def.operation : operationDefKind;
}

export function getOperationName(def: any) {
  const operationDefName =
    def.kind === 'FragmentDefinition' && !!def.name ? def.name.value : 'unknown';
  return def.kind === 'OperationDefinition' && !!def.name ? def.name.value : operationDefName;
}
