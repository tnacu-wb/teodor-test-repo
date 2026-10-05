import GraphiQL from 'graphiql';
import GraphiQLExplorer from 'graphiql-explorer';
import 'graphiql/graphiql.css';
import type { GraphQLSchema } from 'graphql';
import { buildClientSchema, getIntrospectionQuery, parse } from 'graphql';
import { useEffect, useState } from 'react';

import {
  fetcher,
  getDefaultScalarArgValue,
  getOperationKind,
  getOperationName,
  makeDefaultArg,
} from '~utils/graphql';

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

export default function GraphiQLClient() {
  const [schema, setSchema] = useState<GraphQLSchema>();
  const [query, setQuery] = useState(DEFAULT_QUERY);
  const [isExplorerOpen, setIsExplorerOpen] = useState(true);
  useEffect(() => {
    fetchSchema();

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

  return (
    <div className="container graphiql-container">
      <GraphiQLExplorer
        fetcher={fetcher}
        schema={schema}
        query={query}
        onEdit={(input: string) => setQuery(input)}
        onRunOperation={(operationName: any) => graphiQLLegacyRef.handleRunQuery(operationName)}
        explorerIsOpen={isExplorerOpen}
        onToggleExplorer={() => setIsExplorerOpen(!isExplorerOpen)}
        getDefaultScalarArgValue={getDefaultScalarArgValue}
        makeDefaultArg={makeDefaultArg}
      />
      <GraphiQL
        fetcher={fetcher}
        schema={schema}
        query={query}
        ref={(ref) => (graphiQLLegacyRef = ref)}
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
            onClick={() => setIsExplorerOpen(!isExplorerOpen)}
            label="Explorer"
            title="Toggle Explorer"
          />
        </GraphiQL.Toolbar>
      </GraphiQL>
    </div>
  );
}
