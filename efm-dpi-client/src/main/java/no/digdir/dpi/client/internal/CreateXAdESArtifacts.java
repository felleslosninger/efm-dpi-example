package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.AsicEAttachable;
import no.digdir.dpi.client.domain.BusinessCertificate;
import no.digdir.dpi.client.etsi.*;
import org.springframework.stereotype.Component;
import org.w3._2000._09.xmldsig_.DigestMethod;
import org.w3._2000._09.xmldsig_.X509IssuerSerialType;

import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

@Component
@RequiredArgsConstructor
public class CreateXAdESArtifacts {

    private static final DigestMethod sha1DigestMethod = DigestMethod.builder()
            .withContent(emptyList())
            .withAlgorithm(javax.xml.crypto.dsig.DigestMethod.SHA1)
            .build();

    private final Clock clock;

    XAdESArtifacts createArtifactsToSign(List<AsicEAttachable> files, BusinessCertificate sertifikat) {
        byte[] certificateDigestValue = sha1(sertifikat.getEncoded());
        X509Certificate certificate = sertifikat.getX509Certificate();

        DigestAlgAndValueType certificateDigest = DigestAlgAndValueType.builder()
                .withDigestMethod(sha1DigestMethod)
                .withDigestValue(certificateDigestValue)
                .build();

        X509IssuerSerialType certificateIssuer = X509IssuerSerialType.builder()
                .withX509IssuerName(certificate.getIssuerDN().getName())
                .withX509SerialNumber(certificate.getSerialNumber())
                .build();

        SigningCertificate signingCertificate = SigningCertificate.builder()
                .addCerts(CertIDType.builder()
                        .withCertDigest(certificateDigest)
                        .withIssuerSerial(certificateIssuer)
                        .build())
                .build();

        ZonedDateTime now = ZonedDateTime.now(clock);
        SignedSignatureProperties signedSignatureProperties = SignedSignatureProperties.builder()
                .withSigningTime(now)
                .withSigningCertificate(signingCertificate)
                .build();

        SignedDataObjectProperties signedDataObjectProperties = SignedDataObjectProperties
                .builder()
                .withDataObjectFormats(dataObjectFormats(files))
                .build();

        SignedProperties signedProperties = SignedProperties.builder()
                .withSignedSignatureProperties(signedSignatureProperties)
                .withSignedDataObjectProperties(signedDataObjectProperties)
                .withId("SignedProperties")
                .build();
        QualifyingProperties qualifyingProperties = QualifyingProperties.builder()
                .withSignedProperties(signedProperties)
                .withTarget("#Signature")
                .build();

        return XAdESArtifacts.from(qualifyingProperties);
    }

    private List<DataObjectFormat> dataObjectFormats(List<AsicEAttachable> files) {
        List<DataObjectFormat> result = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String signatureElementIdReference = "#ID_" + i;
            result.add(DataObjectFormat.builder()
                    .withMimeType(files.get(i).getMimeType()).withObjectReference(signatureElementIdReference).build());
        }
        return result;
    }
}