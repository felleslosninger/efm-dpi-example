package no.digdir.dpi.client.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.digdir.dpi.client.domain.CmsEncryptedAsice;
import no.digdir.dpi.client.domain.Shipment;
import no.digdir.dpi.client.internal.pipes.Plumber;
import no.digdir.dpi.client.internal.pipes.PromiseMaker;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCmsEncryptedAsice {

    private final PromiseMaker promiseMaker;
    private final InMemoryWithTempFileFallbackResourceFactory resourceFactory;
    private final Plumber plumber;
    private final CreateASiCE createASiCE;
    private final CreateCMSDocument createCMS;

    public CmsEncryptedAsice createCmsEncryptedAsice(Shipment shipment) {
        return promiseMaker.promise(reject -> {
            InMemoryWithTempFileFallbackResource cms = resourceFactory.getResource("dpi-", ".asic.cms");

            plumber.pipe("Creating ASiC-E", inlet -> createASiCE.createAsice(shipment, inlet), reject)
                    .andFinally(outlet -> {
                        try (OutputStream outputStream = cms.getOutputStream()) {
                            createCMS.createCMS(outlet, outputStream, shipment.getReceiverBusinessCertificate());
                        } catch (IOException e) {
                            throw new CreateCmsEncryptedAsice.Exception("CMS encryption failed!", e);
                        }
                    });

            return new CmsEncryptedAsice(cms);
        }).await();
    }

    private static class Exception extends RuntimeException {
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
