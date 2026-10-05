import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Complete payment success")
    request {
        method 'POST'
        url 'payment/hotels/testSessionId/complete?env=https://whatever.premierinn.com'
        body(['PaRes=invalidPares'])
        headers {
            header('Content-Type', 'application/x-www-form-urlencoded; charset=utf-8')
        }
    }

    response {
        status 200
        body('''<html>
<head>
    <title>3-D Secure Authentication</title>
</head>
<body>
    <script type="text/javascript">
        <!--
        try {
            parent.postMessage("{\\"sessionId\\": \\"testSessionId\\", \\"valid\\": false, \\"paymentAuthenticationResponse\\": \\"invalidPares\\"}", "https://whatever.premierinn.com");
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
