package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import no.digdir.dpi.client.domain.Sertifikat;
import no.digdir.dpi.client.exception.KonfigurasjonException;
import no.digdir.dpi.client.exception.RuntimeIOException;
import no.digdir.dpi.client.internal.domain.CMSDocument;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OutputEncryptor;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;

@RequiredArgsConstructor
public class CreateCMSDocument {

    private final ASN1ObjectIdentifier cmsEncryptionAlgorithm;
    private final AlgorithmIdentifier keyEncryptionScheme;

    public CMSDocument createCMS(byte[] bytes, Sertifikat sertifikat) {
        try {
            JceKeyTransRecipientInfoGenerator recipientInfoGenerator = new JceKeyTransRecipientInfoGenerator(sertifikat.getX509Certificate(), keyEncryptionScheme)
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME);

            CMSEnvelopedDataGenerator envelopedDataGenerator = new CMSEnvelopedDataGenerator();
            envelopedDataGenerator.addRecipientInfoGenerator(recipientInfoGenerator);

            OutputEncryptor contentEncryptor = new JceCMSContentEncryptorBuilder(cmsEncryptionAlgorithm).build();
            CMSEnvelopedData cmsData = envelopedDataGenerator.generate(new CMSProcessableByteArray(bytes),
                    contentEncryptor);

            return new CMSDocument(cmsData.getEncoded());

        } catch (CertificateEncodingException e) {
            throw new KonfigurasjonException("Feil med mottakers sertifikat", e);
        } catch (CMSException e) {
            throw new KonfigurasjonException("Kunne ikke generere Cryptographic Message Syntax for dokumentpakke", e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
