import { NodeSDK } from '@opentelemetry/sdk-node';
import { getNodeAutoInstrumentations } from '@opentelemetry/auto-instrumentations-node';
import { GraphQLInstrumentation } from '@opentelemetry/instrumentation-graphql';
import { Resource } from '@opentelemetry/resources';
import createLogger from '../log/logger';
import { basename } from 'path';
import { BatchSpanProcessor, ConsoleSpanExporter } from '@opentelemetry/sdk-trace-base';
import { NodeTracerProvider } from '@opentelemetry/sdk-trace-node';
import { ATTR_SERVICE_NAME, ATTR_SERVICE_VERSION } from '@opentelemetry/semantic-conventions';
import { OTLPTraceExporter } from '@opentelemetry/exporter-trace-otlp-http';
import { config } from '../config/configuration';

const log = createLogger(basename(__filename));
switch (config.ENABLE_OPEN_TELEMETRY) {
  case 'console':
    log.info('Tracing will be exported to the console.');
    const consoleSpanExporter = new ConsoleSpanExporter();

    const consoleBatchProcessor = new BatchSpanProcessor(consoleSpanExporter, {
      scheduledDelayMillis: Number(config.SCHEDULED_DELAY_MILLIS), // Delay in ms before exporting spans
      maxQueueSize: Number(config.MAX_QUEUE_SIZE), // Max spans to keep in the buffer
      maxExportBatchSize: Number(config.MAX_EXPORT_BATCH_SIZE), // Max spans to export in a single batch
      exportTimeoutMillis: Number(config.EXPORT_TIMEOUT_MILLIS) // Timeout for sending a batch in ms
    });

    const consoleProvider = new NodeTracerProvider({
      resource: new Resource({
        [ATTR_SERVICE_NAME]: 'restaurant-apollo-subgraph',
        [ATTR_SERVICE_VERSION]: '1.0.0'
      }),
      spanProcessors: [consoleBatchProcessor] // Configure the batch processor here
    });

    consoleProvider.register(); // Register the provider globally
    break;
  case 'dynatrace':
    log.info('Tracing will be exported to Dynatrace.');
    const otlpTraceExporter = new OTLPTraceExporter({
      url: config.DYNATRACE_URL,
      headers: {
        Authorization: config.DYNATRACE_TOKEN || ''
      }
    });

    const otlpBatchProcessor = new BatchSpanProcessor(otlpTraceExporter, {
      scheduledDelayMillis: Number(config.SCHEDULED_DELAY_MILLIS), // Delay in ms before exporting spans
      maxQueueSize: Number(config.MAX_QUEUE_SIZE), // Max spans to keep in the buffer
      maxExportBatchSize: Number(config.MAX_EXPORT_BATCH_SIZE), // Max spans to export in a single batch
      exportTimeoutMillis: Number(config.EXPORT_TIMEOUT_MILLIS) // Timeout for sending a batch in ms
    });

    const otlpProvider = new NodeTracerProvider({
      resource: new Resource({
        [ATTR_SERVICE_NAME]: 'restaurant-apollo-subgraph',
        [ATTR_SERVICE_VERSION]: '1.0.0'
      }),
      spanProcessors: [otlpBatchProcessor] // Configure the batch processor here
    });

    otlpProvider.register();
    break;
  case 'none':
    log.info(`Apollo server is running without OpenTelemetry`);
    break;
  default:
    log.error(
      `Invalid OpenTelemetry configuration: ${config.ENABLE_OPEN_TELEMETRY}, Set it to 'console', 'dynatrace', or 'none'`
    );
    break;
}

// Add the GraphQL instrumentation alongside automatic ones
const graphqlInstrumentation = new GraphQLInstrumentation({
  depth: 3, // Control the depth of resolver spans
  mergeItems: true, // Merge resolver results into the same span if possible
  responseHook: (span, operation) => {
    // Check and attach query variables if they exist
    if (operation?.variables) {
      span.setAttribute('graphql.variables', JSON.stringify(operation.variables));
    }
    // Optionally capture errors
    if (operation?.errors) {
      span.setAttribute('graphql.errors', JSON.stringify(operation.errors));
    }
  }
});

const instrumentations = [
  getNodeAutoInstrumentations({
    '@opentelemetry/instrumentation-http': {
      enabled: true
    },
    '@opentelemetry/instrumentation-express': {
      enabled: false
    },
    '@opentelemetry/instrumentation-koa': {
      enabled: false
    },
    '@opentelemetry/instrumentation-mysql': {
      enabled: false
    },
    '@opentelemetry/instrumentation-mongodb': {
      enabled: false
    },
    '@opentelemetry/instrumentation-redis': {
      enabled: false
    },
    '@opentelemetry/instrumentation-graphql': {
      enabled: false
    },
    '@opentelemetry/instrumentation-grpc': {
      enabled: false
    },
    '@opentelemetry/instrumentation-ioredis': {
      enabled: false
    },
    '@opentelemetry/instrumentation-net': {
      enabled: false
    },
    '@opentelemetry/instrumentation-dns': {
      enabled: false
    },
    '@opentelemetry/instrumentation-pg': {
      enabled: false
    },
    '@opentelemetry/instrumentation-mongoose': {
      enabled: false
    },
    '@opentelemetry/instrumentation-amqplib': {
      enabled: false
    },
    '@opentelemetry/instrumentation-kafkajs': {
      enabled: false
    }
  }),
  graphqlInstrumentation // Add GraphQL instrumentation explicitly
];

// Initialize the OpenTelemetry SDK only when needed
let sdk: NodeSDK | null = null;

if (config.ENABLE_OPEN_TELEMETRY === 'console' || config.ENABLE_OPEN_TELEMETRY === 'dynatrace') {
  sdk = new NodeSDK({
    instrumentations: instrumentations
  });

  try {
    log.info('Server is running with OpenTelemetry ...');
    sdk.start();
  } catch (error) {
    log.error(`Failed to start OpenTelemetry: ${error}`);
  }
}

// Proper shutdown for containerized services handling
process.on('SIGTERM', async () => {
  if (sdk) {
    try {
      await sdk.shutdown();
      log.info('Tracing gracefully shut down.');
    } catch (error) {
      log.info(`Error during shutdown: ${error}`);
    }
  }
});
