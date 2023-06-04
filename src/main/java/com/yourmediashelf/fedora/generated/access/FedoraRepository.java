//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.08 at 01:10:14 AM SGT 
//


package com.yourmediashelf.fedora.generated.access;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}repositoryName"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}repositoryBaseURL"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}repositoryVersion"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}repositoryPID"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}repositoryOAI-identifier"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}sampleSearch-URL"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}sampleAccess-URL"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}sampleOAI-URL"/>
 *         &lt;element ref="{http://www.fedora.info/definitions/1/0/access/}adminEmail" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "repositoryName",
    "repositoryBaseURL",
    "repositoryVersion",
    "repositoryPID",
    "repositoryOAIIdentifier",
    "sampleSearchURL",
    "sampleAccessURL",
    "sampleOAIURL",
    "adminEmail"
})
@XmlRootElement(name = "fedoraRepository")
public class FedoraRepository {

    @XmlElement(required = true)
    protected String repositoryName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String repositoryBaseURL;
    @XmlElement(required = true)
    protected String repositoryVersion;
    @XmlElement(required = true)
    protected RepositoryPID repositoryPID;
    @XmlElement(name = "repositoryOAI-identifier", required = true)
    protected RepositoryOAIIdentifier repositoryOAIIdentifier;
    @XmlElement(name = "sampleSearch-URL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String sampleSearchURL;
    @XmlElement(name = "sampleAccess-URL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String sampleAccessURL;
    @XmlElement(name = "sampleOAI-URL", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String sampleOAIURL;
    @XmlElement(required = true)
    protected List<String> adminEmail;

    /**
     * Gets the value of the repositoryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * Sets the value of the repositoryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryName(String value) {
        this.repositoryName = value;
    }

    /**
     * Gets the value of the repositoryBaseURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryBaseURL() {
        return repositoryBaseURL;
    }

    /**
     * Sets the value of the repositoryBaseURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryBaseURL(String value) {
        this.repositoryBaseURL = value;
    }

    /**
     * Gets the value of the repositoryVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryVersion() {
        return repositoryVersion;
    }

    /**
     * Sets the value of the repositoryVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryVersion(String value) {
        this.repositoryVersion = value;
    }

    /**
     * Gets the value of the repositoryPID property.
     * 
     * @return
     *     possible object is
     *     {@link RepositoryPID }
     *     
     */
    public RepositoryPID getRepositoryPID() {
        return repositoryPID;
    }

    /**
     * Sets the value of the repositoryPID property.
     * 
     * @param value
     *     allowed object is
     *     {@link RepositoryPID }
     *     
     */
    public void setRepositoryPID(RepositoryPID value) {
        this.repositoryPID = value;
    }

    /**
     * Gets the value of the repositoryOAIIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link RepositoryOAIIdentifier }
     *     
     */
    public RepositoryOAIIdentifier getRepositoryOAIIdentifier() {
        return repositoryOAIIdentifier;
    }

    /**
     * Sets the value of the repositoryOAIIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link RepositoryOAIIdentifier }
     *     
     */
    public void setRepositoryOAIIdentifier(RepositoryOAIIdentifier value) {
        this.repositoryOAIIdentifier = value;
    }

    /**
     * Gets the value of the sampleSearchURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleSearchURL() {
        return sampleSearchURL;
    }

    /**
     * Sets the value of the sampleSearchURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleSearchURL(String value) {
        this.sampleSearchURL = value;
    }

    /**
     * Gets the value of the sampleAccessURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleAccessURL() {
        return sampleAccessURL;
    }

    /**
     * Sets the value of the sampleAccessURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleAccessURL(String value) {
        this.sampleAccessURL = value;
    }

    /**
     * Gets the value of the sampleOAIURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleOAIURL() {
        return sampleOAIURL;
    }

    /**
     * Sets the value of the sampleOAIURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleOAIURL(String value) {
        this.sampleOAIURL = value;
    }

    /**
     * Gets the value of the adminEmail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adminEmail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdminEmail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAdminEmail() {
        if (adminEmail == null) {
            adminEmail = new ArrayList<String>();
        }
        return this.adminEmail;
    }

}
