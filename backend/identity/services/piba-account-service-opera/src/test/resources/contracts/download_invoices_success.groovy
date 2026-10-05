package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get Download Invoices List")
    request {
        method 'GET'
        url '/piba/account/invoices/download/783582/1a409f7d-a66c-4991-a424-97b7ce0e6cf9/22094'
        headers {
            header('Content-Type', 'application/pdf')
            header('session-id', '13245312321345')
            header("Authorization", "Bearer dummy_value10")
        }
    }

    response {
        status 200
        headers {
            header('Content-Disposition', 'attachment; filename=rptInvoice7835822021531.pdf')
        }
    }
}
