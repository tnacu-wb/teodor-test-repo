import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should initSession secured")
    request {
        method 'POST'
        url '/auth/session?business=true'
        body(
                '''
           {
              "guestHistoryNumber": "136e97d9f612e8407fda1c6a378e7e40Fib2j5GRY6Tq9rqvQ9rk9g=="
           }
            '''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body(
                "sessionId": "a9VlzUuCVhceE1P0"
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}