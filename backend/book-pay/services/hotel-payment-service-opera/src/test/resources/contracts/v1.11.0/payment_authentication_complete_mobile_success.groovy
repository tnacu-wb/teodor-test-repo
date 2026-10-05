import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Complete a 3DS 2.1 payment for mobile channel")
    request {
        method 'POST'
        url '/payment/authentication/PGGRVx9umpr5NW5i/complete?bookingChannel=MOBILE'
        body(['result=SUCCESS','order.id=344444', 'transaction.id=1', 'result=SUCCESS', 'response.gatewayRecommendation=PROCEED', 'env=*'])
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
            parent.postMessage("{\\"threeDSecureV2\\": true, \\"sessionId\\": \\"PGGRVx9umpr5NW5i\\", \\"datacashReference\\":  \\"344444\\", \\"gatewayRecommendation\\": \\"PROCEED\\", \\"transactionId\\":  \\"1\\", \\"success\\":  true}", "*");
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