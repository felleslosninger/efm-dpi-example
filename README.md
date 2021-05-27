# efm-dpi-example

Følgende properties må settes:

example.dpi.keystore.path
example.dpi.keystore.alias
example.dpi.keystore.password

Så kan du kjøre DpiExampleApplication med -c (businessCertificate) opsjonen og minst en fil.
Hvis mer enn en fil spesifisere, så vil den første bli brukt som hoveddokument og de resterende blir vedlegg. 

Eksempel:

-c ./receivercertificate.cer ./maindocument.pdf ./attachment1.pdf ./attachment2.jpg

