/* eslint-disable @typescript-eslint/no-var-requires */
const HttpApi = require('i18next-http-backend/cjs');
const { gql, request } = require('graphql-request');

module.exports = {
  i18n: {
    defaultLocale: 'gb',
    locales: ['gb', 'de'],
  },
  reloadOnPrerender: process.env.NODE_ENV === 'development' ? true : false,
  ns: ['common', 'example'],
  debug: false,
  use: [HttpApi],
  backend: {
    loadPath: '{{lng}}',
    request: function (options, lng, payload, callback) {
      const endpoint = process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT;
      const query =
        lng === 'gb'
          ? gql`
              query getLabelsByLanguage {
                labels(country: "gb", language: "en") {
                  booking
                  main
                  piBookings
                }
              }
            `
          : gql`
              query getLabelsByLanguage {
                labels(country: "de", language: "de") {
                  booking
                  main
                  piBookings
                }
              }
            `;

      const headers = {};

      request({
        url: endpoint,
        document: query,
        requestHeaders: headers,
      })
        .then((data) => {
          const main = JSON.parse(data.labels.main);
          const piBookings = JSON.parse(data.labels.piBookings);
          const booking = JSON.parse(data.labels.booking);
          const labels = { ...main, ...piBookings, ...booking };

          callback(null, {
            data: JSON.stringify(labels),
            status: 200,
          });
        })
        .catch((err) => callback(err));
    },
    parse: function (data) {
      return JSON.parse(data);
    },
  },
  serializeConfig: false,
};
