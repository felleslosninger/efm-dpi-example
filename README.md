# efm-dpi-example

Følgende properties må settes:

example.dpi.keystore.path
example.dpi.keystore.alias
example.dpi.keystore.password

Så kan du kjøre DpiExampleApplication med -s (sertifikat) opsjonen og minst en fil.
Hvis mer enn en fil spesifisere, så vil den første bli brukt som hoveddokument og de resterende blir vedlegg. 

Eksempel:

-s ./virksomhetsertifikat.pem ./hoveddokument.pdf ./vedlegg1.pdf ./vedlegg2.jpg

