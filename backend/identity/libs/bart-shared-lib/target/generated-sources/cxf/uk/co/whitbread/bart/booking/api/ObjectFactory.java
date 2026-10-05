
package uk.co.whitbread.bart.booking.api;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.whitbread.bart.booking.api package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.whitbread.bart.booking.api
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidateBin }
     * 
     * @return
     *     the new instance of {@link ValidateBin }
     */
    public ValidateBin createValidateBin() {
        return new ValidateBin();
    }

    /**
     * Create an instance of {@link BinRequest }
     * 
     * @return
     *     the new instance of {@link BinRequest }
     */
    public BinRequest createBinRequest() {
        return new BinRequest();
    }

    /**
     * Create an instance of {@link ValidateBinResponse }
     * 
     * @return
     *     the new instance of {@link ValidateBinResponse }
     */
    public ValidateBinResponse createValidateBinResponse() {
        return new ValidateBinResponse();
    }

    /**
     * Create an instance of {@link BinResponse }
     * 
     * @return
     *     the new instance of {@link BinResponse }
     */
    public BinResponse createBinResponse() {
        return new BinResponse();
    }

    /**
     * Create an instance of {@link Price }
     * 
     * @return
     *     the new instance of {@link Price }
     */
    public Price createPrice() {
        return new Price();
    }

}
