
package uk.co.whitbread.bart.cancellation.api;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.whitbread.bart.cancellation.api package. 
 * <p>An ObjectFactory allows you to programmatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.whitbread.bart.cancellation.api
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CancellationRequest }
     * 
     * @return
     *     the new instance of {@link CancellationRequest }
     */
    public CancellationRequest createCancellationRequest() {
        return new CancellationRequest();
    }

    /**
     * Create an instance of {@link CancellationRequest2 }
     * 
     * @return
     *     the new instance of {@link CancellationRequest2 }
     */
    public CancellationRequest2 createCancellationRequest2() {
        return new CancellationRequest2();
    }

    /**
     * Create an instance of {@link CancellationRequestResponse }
     * 
     * @return
     *     the new instance of {@link CancellationRequestResponse }
     */
    public CancellationRequestResponse createCancellationRequestResponse() {
        return new CancellationRequestResponse();
    }

    /**
     * Create an instance of {@link CancellationResponse }
     * 
     * @return
     *     the new instance of {@link CancellationResponse }
     */
    public CancellationResponse createCancellationResponse() {
        return new CancellationResponse();
    }

    /**
     * Create an instance of {@link ArrayOfConfirmationConfirmation }
     * 
     * @return
     *     the new instance of {@link ArrayOfConfirmationConfirmation }
     */
    public ArrayOfConfirmationConfirmation createArrayOfConfirmationConfirmation() {
        return new ArrayOfConfirmationConfirmation();
    }

    /**
     * Create an instance of {@link Confirmation }
     * 
     * @return
     *     the new instance of {@link Confirmation }
     */
    public Confirmation createConfirmation() {
        return new Confirmation();
    }

    /**
     * Create an instance of {@link Refund }
     * 
     * @return
     *     the new instance of {@link Refund }
     */
    public Refund createRefund() {
        return new Refund();
    }

    /**
     * Create an instance of {@link ArrayOfconfirmationConfirmationSent }
     * 
     * @return
     *     the new instance of {@link ArrayOfconfirmationConfirmationSent }
     */
    public ArrayOfconfirmationConfirmationSent createArrayOfconfirmationConfirmationSent() {
        return new ArrayOfconfirmationConfirmationSent();
    }

    /**
     * Create an instance of {@link ConfirmationSent }
     * 
     * @return
     *     the new instance of {@link ConfirmationSent }
     */
    public ConfirmationSent createConfirmationSent() {
        return new ConfirmationSent();
    }

    /**
     * Create an instance of {@link ErrorDetails }
     * 
     * @return
     *     the new instance of {@link ErrorDetails }
     */
    public ErrorDetails createErrorDetails() {
        return new ErrorDetails();
    }

}
