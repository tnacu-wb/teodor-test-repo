import { config } from './apollo/config/configuration';
import './apollo/telemetry/opentelemetry';
import { basename } from 'path';
import http, { IncomingHttpHeaders } from 'http';
import { ApolloServer } from '@apollo/server';
import { mergeSchemas } from '@graphql-tools/schema';
import createLogger from './apollo/log/logger';
import cors from 'cors';
import express from 'express';
import bodyParser from 'body-parser';
import { formatError } from './apollo/exception/error-handler';
import { contentEntitySubgraphResolvers } from './apollo/subgraphs/content-entity-service/subgraph-resolvers';
import { ruleAgentEntitySubgraphResolvers } from './apollo/subgraphs/rules-agent-entity-service/subgraph-resolvers';
import { paymentInfoSubgraphResolvers } from './apollo/subgraphs/payment-info-messages-pipeline/subgraph-resolvers';
import { hotelEntitySubgraphResolvers } from './apollo/subgraphs/hotel-entity-service/subgraph-resolvers';
import { reservationManagerEntitySubgraphResolvers } from './apollo/subgraphs/reservation-manager-entity-service/subgraph-resolvers';
import { companyEntitySubgraphResolvers } from './apollo/subgraphs/company-entity-service/subgraph-resolvers';
import { marketingServiceSubgraphResolvers } from './apollo/subgraphs/marketing-service-opera/subgraph-resolvers';
import { traceIdMiddleware } from './apollo/middleware/trace-id-middleware';
import { companyReportingSubgraphResolvers } from './apollo/subgraphs/company-reporting-service/subgraph-resolvers';
import { hotelCardServiceSubgraphResolvers } from './apollo/subgraphs/hotel-card-service-opera/subgraph-resolvers';
import { kioskCheckinServiceSubgraphResolvers } from './apollo/subgraphs/kiosk-checkin-service/subgraph-resolvers';
import { basketSubgraphResolvers } from './apollo/subgraphs/basket-service/subgraph-resolvers';
import { pibaAccountServiceSubgraphResolvers } from './apollo/subgraphs/piba-account-service-opera/subgraph-resolvers';
import { addressLookupEntitySubgraphResolvers } from './apollo/subgraphs/address-lookup-entity-service/subgraph-resolvers';
import { companyServiceOperaSubgraphResolvers } from './apollo/subgraphs/company-service-opera/subgraph-resolvers';
import { hotelReservationEntitySubgraphResolvers } from './apollo/subgraphs/hotel-reservation-entity-service/subgraph-resolvers';
import { businessTetherServiceSubgraphResolvers } from './apollo/subgraphs/business-tether-service-opera/subgraph-resolvers';
import { companyEmployeeServiceSubgraphResolvers } from './apollo/subgraphs/company-employee-service-opera/subgraph-resolvers';
import { feedbackServiceOperaSubgraphResolvers } from './apollo/subgraphs/feedback-service-opera/subgraph-resolvers';
import { accountEntitySubgraphResolvers } from './apollo/subgraphs/account-entity-service/subgraph-resolvers';
import { hotelAccountServiceSubgraphResolvers } from './apollo/subgraphs/hotel-account-service-opera/subgraph-resolvers';
import { spendingEntityServiceSubgraphResolvers } from './apollo/subgraphs/spending-entity-service/subgraph-resolvers';
import { initiatePaymentSubgraphResolvers } from './apollo/subgraphs/initiate-payment-pipeline/subgraph-resolver';
import { hotelDashboardServiceSubgraphResolvers } from './apollo/subgraphs/hotel-dashboard-service-opera/subgraph-resolvers';
import { paymentMethodsEntitySubgraphResolvers } from './apollo/subgraphs/payment-methods-entity-service/subgraph-resolvers';
import { hotelAvailabilitiesSubgraphResolvers } from './apollo/subgraphs/hotel-availabilities-pipeline/subgraph-resolver';
import { bookingInformationSubgraphResolvers } from './apollo/subgraphs/booking-information-pipeline/subgraph-resolvers';
import { promotionPanelSubgraphResolvers } from './apollo/subgraphs/promotion-panel-pipeline/subgraph-resolvers';
import { termsAndConditionsSubgraphResolvers } from './apollo/subgraphs/terms-and-conditions-pipeline/subgraph-resolver';
import { donationsSubgraphResolvers } from './apollo/subgraphs/donations-pipeline/subgraph-resolvers';
import { bookingConfirmationSubgraphResolvers } from './apollo/subgraphs/booking-confirmation-pipeline/subgraph-resolvers';
import { privacyPolicySubgraphResolvers } from './apollo/subgraphs/privacy-policy-pipeline/subgraph-resolver';
import { packagesSubgraphResolvers } from './apollo/subgraphs/packages-pipeline/subgraph-resolver';
import { payAppEntitySubgraphResolvers } from './apollo/subgraphs/pay-app-entity-service/subgraph-resolvers';
import { availabilityCacheSubgraphResolvers } from './apollo/subgraphs/availability-cache-service-opera/subgraph-resolvers';
import { hotelRegisterServiceSubgraphResolvers } from './apollo/subgraphs/hotel-register-service-opera/subgraph-resolvers';
import { pibaRegistrationServiceSubgraphResolvers } from './apollo/subgraphs/piba-registration-service-opera/subgraph-resolvers';
import { digitalKeyServiceSubgraphResolvers } from './apollo/subgraphs/digital-key-service/subgraph-resolvers';
import { promoServiceSubgraphResolvers } from './apollo/subgraphs/promo-service/subgraph-resolver';

const log = createLogger(basename(__filename));
const getApolloSandboxHtml = (endpoint: string) => `
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Apollo Sandbox</title>
    <style>
      body {
        margin: 0;
      }
      #sandbox {
        height: 100vh;
        width: 100vw;
      }
    </style>
  </head>
  <body>
    <div id="sandbox"></div>
    <script src="https://embeddable-sandbox.cdn.apollographql.com/_latest/embeddable-sandbox.umd.production.min.js"></script>
    <script>
      new window.EmbeddedSandbox({
        target: '#sandbox',
        initialEndpoint: window.location.origin + '${endpoint}',
        includeCookies: true
      });
    </script>
  </body>
</html>`;

const combinedSchema = mergeSchemas({
  schemas: [
    contentEntitySubgraphResolvers(),
    hotelAvailabilitiesSubgraphResolvers(),
    hotelEntitySubgraphResolvers(),
    hotelReservationEntitySubgraphResolvers(),
    ruleAgentEntitySubgraphResolvers(),
    paymentInfoSubgraphResolvers(),
    reservationManagerEntitySubgraphResolvers(),
    companyEntitySubgraphResolvers(),
    marketingServiceSubgraphResolvers(),
    companyReportingSubgraphResolvers(),
    hotelCardServiceSubgraphResolvers(),
    basketSubgraphResolvers(),
    kioskCheckinServiceSubgraphResolvers(),
    addressLookupEntitySubgraphResolvers(),
    pibaAccountServiceSubgraphResolvers(),
    companyServiceOperaSubgraphResolvers(),
    businessTetherServiceSubgraphResolvers(),
    feedbackServiceOperaSubgraphResolvers(),
    companyEmployeeServiceSubgraphResolvers(),
    accountEntitySubgraphResolvers(),
    hotelAccountServiceSubgraphResolvers(),
    spendingEntityServiceSubgraphResolvers(),
    hotelDashboardServiceSubgraphResolvers(),
    initiatePaymentSubgraphResolvers(),
    packagesSubgraphResolvers(),
    paymentMethodsEntitySubgraphResolvers(),
    bookingInformationSubgraphResolvers(),
    promotionPanelSubgraphResolvers(),
    termsAndConditionsSubgraphResolvers(),
    donationsSubgraphResolvers(),
    bookingConfirmationSubgraphResolvers(),
    privacyPolicySubgraphResolvers(),
    payAppEntitySubgraphResolvers(),
    availabilityCacheSubgraphResolvers(),
    hotelRegisterServiceSubgraphResolvers(),
    pibaRegistrationServiceSubgraphResolvers(),
    digitalKeyServiceSubgraphResolvers(),
    promoServiceSubgraphResolvers()
  ]
});

const createApolloServer = async (schema: any) => {
  const server = new ApolloServer({
    schema,
    introspection: config.INTROSPECTION === 'true',
    formatError
  });
  await server.start();
  return server;
};

(async () => {
  try {
    log.info(`Server is configured with introspection: ${config.INTROSPECTION}`);
    const app = express();

    let isReady = false;
    const httpServer = http.createServer(app);
    const server = await createApolloServer(combinedSchema);
    isReady = true;
    const path = `/graphql`;

    app.use(traceIdMiddleware);
    const allowedOrigins = (config.CORS_ALLOWED_ORIGINS || '')
      .split(',')
      .map((origin) => origin.trim())
      .filter(Boolean);
    app.use(
      cors<cors.CorsRequest>({
        origin: (origin, callback) => {
          if (!origin || allowedOrigins.length === 0) {
            callback(null, true);
            return;
          }
          callback(null, allowedOrigins.includes(origin));
        }
      })
    );
    app.use(bodyParser.json());

    app.post(path, async (req, res) => {
      try {
        const { body } = req;
        const result = await server.executeOperation(body, {
          contextValue: { headers: req.headers, res }
        });

        if (result.body.kind === 'single') {
          res.status(200).json(result.body.singleResult);
        } else {
          res.status(200).json({ errors: [{ message: 'Incremental delivery not supported' }] });
        }
      } catch (error) {
        log.error('Error executing GraphQL operation:', { error });
        res.status(500).json({ errors: [{ message: 'Internal server error' }] });
      }
    });

    app.get('/health', (req, res) => {
      res.status(200).json({ status: 'ok' });
    });

    app.get('/ready', (req, res) => {
      if (isReady) {
        res.status(200).json({ status: 'ready', apollo: 'initialized', schemas: 'loaded' });
      } else {
        res.status(503).json({ status: 'not ready', apollo: 'initializing' });
      }
    });

    app.get(path, async (req, res) => {
      if (config.INTROSPECTION === 'true') {
        res.status(200).type('html').send(getApolloSandboxHtml(path));
      } else {
        res.status(404).send('Not found');
      }
    });

    await new Promise<void>((resolve) => httpServer.listen({ port: Number(config.PORT) }, resolve));
    log.info(`Apollo Subgraphs started....`);
  } catch (error) {
    log.error('Error starting Apollo Server:', { error });
    process.exit(1);
  }
})();
