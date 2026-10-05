
package uk.co.whitbread.bart.business.api;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.whitbread.bart.business.api package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.whitbread.bart.business.api
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FindEmployees }
     * 
     * @return
     *     the new instance of {@link FindEmployees }
     */
    public FindEmployees createFindEmployees() {
        return new FindEmployees();
    }

    /**
     * Create an instance of {@link FindEmployeeRequest }
     * 
     * @return
     *     the new instance of {@link FindEmployeeRequest }
     */
    public FindEmployeeRequest createFindEmployeeRequest() {
        return new FindEmployeeRequest();
    }

    /**
     * Create an instance of {@link FindEmployeesResponse }
     * 
     * @return
     *     the new instance of {@link FindEmployeesResponse }
     */
    public FindEmployeesResponse createFindEmployeesResponse() {
        return new FindEmployeesResponse();
    }

    /**
     * Create an instance of {@link FindEmployee1Response }
     * 
     * @return
     *     the new instance of {@link FindEmployee1Response }
     */
    public FindEmployee1Response createFindEmployee1Response() {
        return new FindEmployee1Response();
    }

    /**
     * Create an instance of {@link ArrayOfFindEmployee1FindEmployee1 }
     * 
     * @return
     *     the new instance of {@link ArrayOfFindEmployee1FindEmployee1 }
     */
    public ArrayOfFindEmployee1FindEmployee1 createArrayOfFindEmployee1FindEmployee1() {
        return new ArrayOfFindEmployee1FindEmployee1();
    }

    /**
     * Create an instance of {@link FindEmployee1 }
     * 
     * @return
     *     the new instance of {@link FindEmployee1 }
     */
    public FindEmployee1 createFindEmployee1() {
        return new FindEmployee1();
    }

    /**
     * Create an instance of {@link Person }
     * 
     * @return
     *     the new instance of {@link Person }
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link ArrayOferrorError }
     * 
     * @return
     *     the new instance of {@link ArrayOferrorError }
     */
    public ArrayOferrorError createArrayOferrorError() {
        return new ArrayOferrorError();
    }

    /**
     * Create an instance of {@link Error }
     * 
     * @return
     *     the new instance of {@link Error }
     */
    public Error createError() {
        return new Error();
    }

}
