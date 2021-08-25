# efm-dpi-example

Følgende properties må settes:

dpi.client.type   # Tilatte verdier er: file, web - Default er web
dpi.client.uri    # For type=web skal denne være URLen til hjørne2. For type=file skal denne være URLen til katalogen man ønsker å lagre til.
dpi.client.keystore.path
dpi.client.keystore.alias
dpi.client.keystore.password
dpi.client.oidc.clientId

Så kan du kjøre DpiExampleApplication med -c (businessCertificate) opsjonen og minst to filer.
Den første vil bli lest som SBD. Den andre vil tolkes som hoveddoument og de resterende som vedlegg.

Eksempel:

-c ./receiverCertificate.cer ./standardBusinessDocument.sbd ./mainDocument.pdf ./attachment1.pdf ./attachment2.jpg

