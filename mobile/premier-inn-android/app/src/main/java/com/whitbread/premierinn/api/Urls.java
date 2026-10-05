package com.whitbread.premierinn.api;

public interface Urls {

    // LIVE API Urls
    String LIVE_MICRO_SERVICE_DOMAIN = "api.whitbread.co.uk";
    String LIVE_MICRO_SERVICE_URL = "https://" + LIVE_MICRO_SERVICE_DOMAIN;
    String LIVE_OPERA_MICRO_SERVICE_URL = "https://restapi.premierinn.com";

    String PRELIVE_WANDA_URL = "https://api.wanda.premierinn.com";
    String PRELIVE_HULK_URL = "https://api.hulk.premierinn.com";
    String LIVE_GRAPHQL_URL = "https://api.premierinn.com";
    String LIVE_CHECK_IN_ONLINE_URL = "https://secure.premierinn.com/en/prepareCheckinOnline.action?INTCMP=And_CIOL";

    // QA API Urls
    String QA_MICRO_SERVICE_URL = "https://api.qa.eks.whitbread.digital";

    String QA_GRAPHQL_URL
            = "https://api.uat.premierinn.digital"; //OPERA: Remember to Change when we get actual QA URL

    // UAT API Urls
    String UAT_MICRO_SERVICE_DYNAMIC = "api-%s.whitbread.co.uk";

    String UAT_MICRO_SERVICE_DOMAIN = String.format(UAT_MICRO_SERVICE_DYNAMIC, "uat");
    String UAT_MICRO_SERVICE_URL = "https://" + UAT_MICRO_SERVICE_DOMAIN;

    String UAT_CHECK_IN_ONLINE_URL
            = "https://secure.beta.premierinn.digital/en/prepareCheckinOnline.action?INTCMP=And_CIOL";

    // GraphQL Urls
    String GRAPHQL_DIT = "https://api.dit.premierinn.digital";
    String GRAPHQL_SIT = "https://api.sit.premierinn.digital";
    String GRAPHQL_DEMO = "https://api.demo.premierinn.digital";
    String GRAPHQL_PRE_PROD = "https://api.preprod.premierinn.digital";
    String GRAPHQL_PERF = "https://api.perf.premierinn.digital";
    String GRAPHQL_UAT = "https://api.uat.premierinn.digital";

    String GRAPHQL_DIT_REST_ENDPOINT = "https://restapi.dit.premierinn.digital";
    String GRAPHQL_UAT_REST_ENDPOINT = "https://restapi.uat.premierinn.digital";
    String GRAPHQL_DEMO_REST_ENDPOINT = "https://restapi.demo.premierinn.digital";
    String GRAPHQL_SIT_REST_ENDPOINT = "https://restapi.sit.premierinn.digital";
    String GRAPHQL_PERF_REST_ENDPOINT = "https://restapi.perf.premierinn.digital";

    //Common Web Urls
    String CONTENT_BASE_URL = "https://www.premierinn.com";
    String CREDIT_CARD_FORMAT_URL = "https://www.premierinn.com/content/dam/global/booking/%s.jpg";
    String PLAYSTORE = "https://play.google.com/store/apps/details?id=%s";
    String PAYPAL_PAY_FORMAT_URL = "https://www.premierinn.com/content/dam/global/booking/%s.png";
    String GOOGLE_PAY_FORMAT_URL = "https://www.premierinn.com/content/dam/global/booking/%s.png";
}