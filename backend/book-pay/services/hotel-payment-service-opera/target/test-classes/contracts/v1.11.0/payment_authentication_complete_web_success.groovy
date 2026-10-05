import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Complete a 3DS 2.1 payment for web channel")
    request {
        method 'POST'
        url '/payment/authentication/PGGRVx9umpr5NW5i/complete?bookingChannel=WEB&env=https://secure2.premierinn.com.staging.nativ-systems.com'
        body(['order.id=344444', 'transaction.id=1', 'result=SUCCESS', 'response.gatewayRecommendation=PROCEED'])
        headers {
            header('Content-Type', 'application/x-www-form-urlencoded')
        }
    }

    response {
        status 200
        body('''<html>
<head>
    <title>3-D Secure V2 Authentication</title>
</head>
<body>
    <script type="text/javascript">
        <!--
        try {
            parent.postMessage("{\\"threeDSecureV2\\": true, \\"sessionId\\": \\"PGGRVx9umpr5NW5i\\", \\"datacashReference\\":  \\"344444\\", \\"gatewayRecommendation\\": \\"PROCEED\\", \\"transactionId\\":  \\"1\\", \\"success\\":  true}", "https://secure2.premierinn.com.staging.nativ-systems.com");
        } catch (error) {
            if (console) {
                console.log(error);
            }
        }
        // -->
    </script>
</body>
</html>''')
        headers {
            header('Content-Type', 'text/html;charset=UTF-8')
        }
    }
}