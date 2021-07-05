package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Shipment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCmsEncryptedAsice {

    private final InMemoryWithTempFileFallbackResourceFactory resourceFactory;
    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    public CmsEncryptedAsice createCmsEncryptedAsice(Shipment shipment) {
        return new CmsEncryptedAsice(createCMS(shipment, createAsice(shipment)));
    }

    private InMemoryWithTempFileFallbackResource createCMS(Shipment shipment, InMemoryWithTempFileFallbackResource asic) {
        InMemoryWithTempFileFallbackResource cms = resourceFactory.getResource("dpi-", ".asic.cms");

        try (InputStream inputStream = asic.getInputStream(); OutputStream outputStream = cms.getOutputStream()) {
            createCMS.createCMS(inputStream, outputStream, shipment.getReceiverBusinessCertificate());
        } catch (IOException e) {
            throw new Exception("CMS encryption failed!", e);
        }
        return cms;
    }

    private InMemoryWithTempFileFallbackResource createAsice(Shipment shipment) {
        InMemoryWithTempFileFallbackResource asic = resourceFactory.getResource("dpi-", ".asic");

        try (OutputStream outputStream = asic.getOutputStream()) {
            createASiCE.createAsice(shipment, outputStream);
        } catch (IOException e) {
            throw new Exception("Creating ASiC-E failed!", e);
        }
        return asic;
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
