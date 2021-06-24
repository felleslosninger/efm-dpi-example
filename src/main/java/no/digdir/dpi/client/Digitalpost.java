package no.digdir.dpi.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Digitalpost")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "Digitalpost", propOrder = {
    "jwt",
    "binaryContent"
}, namespace="testt")
public class Digitalpost {
    public String jwt;
    public String binaryContent;
    
    public Digitalpost() {
        
    }

    public Digitalpost(String jwt, String binaryContent) {
        this.jwt = jwt;
        this.binaryContent = binaryContent;
    }

    @XmlElement(name = "Jwt")
    public String getJwt() {
        return this.jwt;
    }
    
    @XmlElement(name = "BinaryContent")
    public String getBinaryContent() {
        return this.binaryContent;
    }
}
