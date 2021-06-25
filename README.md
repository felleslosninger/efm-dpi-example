# efm-dpi-example

Følgende properties må settes:

example.dpi.keystore.path
example.dpi.keystore.alias
example.dpi.keystore.password

Så kan du kjøre DpiExampleApplication med -c (businessCertificate) opsjonen og minst en fil.
Hvis mer enn en fil spesifisere, så vil den første bli brukt som hoveddokument og de resterende blir vedlegg. 

Eksempel:

-c ./receivercertificate.cer ./maindocument.pdf ./attachment1.pdf ./attachment2.jpg


## Kjøre mot lokal oxalis-inbound
Du må først starte opp en inbound-prosess på http://localhost:8080/as4 
(kan rekonfigureres i `src/test/resources/application.yml`).
 
Lag en kjørekonfigurasjon for klassen `DpiExampleApplication`med:

1. `Program arguments`: `-c ${system_property:user.home}/.oxalis/peppol_certificate.pem src/test/resources/digital-sbd.json src/test/resources/svada.pdf`
(ett sertifikat, offentlig nøkkel for mottaker-aksesspunkt, og to filer 
1. `VM arguments:`

```
-DOXALIS_HOME=${system_property:user.home}/.oxalis
-Ddpi.client.keystore.password=<KEYSTORE_PASSWORD>
-Doxalis.keystore.path=${system_property:user.home}/.oxalis/conf/peppol_certificate.p12
-Doxalis.keystore.key.alias=cert
-Doxalis.keystore.password=<KEYSTORE_PASSWORD>
-Doxalis.keystore.key.password=<KEYSTORE_PASSWORD>
-Dspring.config.location=src/test/resources/application.yml
```
`KEYSTORE_PASSWORD`er passordet til p12-keystoren vi bruker i peppol-dpi-access-point, `peppol_certificate.p12`.
