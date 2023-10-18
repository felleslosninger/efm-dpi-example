> [!WARNING]
> Please note that this project is not being maintained.
>
> A fork is being maintained under https://github.com/felleslosninger/efm-integrasjonspunkt/tree/development/dpi-client for the purpose of eFormidlings use of DPI.

# efm-dpi-example

The following properties are mandatory:

dpi.client.type   # Allowed values are: file, web - Default is web
dpi.client.uri    # For type=web, this uri is the URL to Corner2 API. For type=file this is the directory to store the files.
dpi.client.keystore.path
dpi.client.keystore.alias
dpi.client.keystore.password

Then you can run the DpiExampleApplication with the -c (businessCertificate) option and at least two files.
The first file is the SBD. The second is interpreted as the main document and the remaining files is attachments.

E.g.:

-c ./receiverCertificate.cer send ./standardBusinessDocument.sbd ./mainDocument.pdf ./attachment1.pdf ./attachment2.jpg



