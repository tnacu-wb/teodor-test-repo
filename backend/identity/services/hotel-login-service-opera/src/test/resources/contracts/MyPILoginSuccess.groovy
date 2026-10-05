import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should login in My PremierInn")
    request {
        method 'POST'
        url '/auth/hotels/login?business=false'
        body(
            '''
           {
                "username": "myuser@whitbread.com",
                "password": "securePa$$w0rd"
           }
           '''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body('''
        {
            "sessionId": "654321",
            "loginSuccessful": true
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}