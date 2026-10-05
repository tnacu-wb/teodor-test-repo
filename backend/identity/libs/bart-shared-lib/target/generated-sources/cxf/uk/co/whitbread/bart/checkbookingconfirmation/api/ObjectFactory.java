
package uk.co.whitbread.bart.checkbookingconfirmation.api;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.whitbread.bart.checkbookingconfirmation.api package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.whitbread.bart.checkbookingconfirmation.api
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CheckBookingConfirmationRequest }
     * 
     * @return
     *     the new instance of {@link CheckBookingConfirmationRequest }
     */
    public CheckBookingConfirmationRequest createCheckBookingConfirmationRequest() {
        return new CheckBookingConfirmationRequest();
    }

    /**
     * Create an instance of {@link CheckBookingConfirmationRequest2 }
     * 
     * @return
     *     the new instance of {@link CheckBookingConfirmationRequest2 }
     */
    public CheckBookingConfirmationRequest2 createCheckBookingConfirmationRequest2() {
        return new CheckBookingConfirmationRequest2();
    }

    /**
     * Create an instance of {@link CheckBookingConfirmationRequestResponse }
     * 
     * @return
     *     the new instance of {@link CheckBookingConfirmationRequestResponse }
     */
    public CheckBookingConfirmationRequestResponse createCheckBookingConfirmationRequestResponse() {
        return new CheckBookingConfirmationRequestResponse();
    }

    /**
     * Create an instance of {@link CheckBookingConfirmationResponse }
     * 
     * @return
     *     the new instance of {@link CheckBookingConfirmationResponse }
     */
    public CheckBookingConfirmationResponse createCheckBookingConfirmationResponse() {
        return new CheckBookingConfirmationResponse();
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
