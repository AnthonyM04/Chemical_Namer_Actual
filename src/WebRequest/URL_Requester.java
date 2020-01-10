package WebRequest;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static WebRequest.RequestType.FORMULA;


public class URL_Requester {
        /*
            PubChem PUG REST API

            - Limit of 5 request per second, which is pretty easy

            URL Specifications:
            https://pubchem.ncbi.nlm.nih.gov/rest/pug/
            <input specification>/
            <operation specification>/
            [<output specification>][?<operation_options>]

            <input specification> = <domain>/<namespace>/<identifiers>

            <domain> = substance | compound | assay | <other inputs>

            <namespace> = cid | name | smiles |
                          inchi | sdf | inchikey | formula |
                          <structure search> | <xref> |
                          listkey | <fast search>

            <structure search> = {substructure | superstructure |
                                 similarity | identity}/
                                 {smiles | inchi | sdf | cid}

            <fast search> = {fastidentity | fastsimilarity_2d | fastsimilarity_3d |
                            fastsubstructure | fastsuperstructure}/{smiles |
                            smarts | inchi | sdf | cid} | fastformula

            substance domain <namespace> = sid | sourceid/<source id> |
                                           sourceall/<source name> | name |
                                           <xref> | listkey

                        <source name> = any valid PubChem depositor name

            <output specification> = XML | ASNT | ASNB | JSON |
                                     JSONP [ ?callback=<callback name> ] | SDF |
                                     CSV | PNG | TXT


            Simply put (using an example request):
            https://pubchem.ncbi.nlm.nih.gov/rest/pug <- prolog (included in every request)
            /compound/name/vioxx                      <- input
            /property/InChI                           <- operation
            /TXT                                      <- output

         */

    private URL infoURL = new URL("https://pubchem.ncbi.nlm.nih.gov/rest/pug");
    private URL picURL = new URL("https://pubchem.ncbi.nlm.nih.gov/rest/pug");
    private RequestType currentType;
    private String requestedProperties = "MolecularFormula,MolecularWeight,IUPACName,IsomericSMILES";



    public URL_Requester(RequestType inputReq, String input) throws IOException {

        // Feel free to delete all the commented code, I just kept it to perserve method
        // for sending POST and GET requests just in case we do end up needing it, but
        // I highly doubt it. If it gets too confusing, you can just delete the commented code.

        // Sets what properties we want


        currentType = inputReq;
        /*
        URL infoURL = new URL("https://pubchem.ncbi.nlm.nih.gov/rest/pug"); // sets the HTTPS url
        URL picURL = new URL("https://pubchem.ncbi.nlm.nih.gov/rest/pug");

        // Opens the connections
        HttpURLConnection infoConnection = (HttpURLConnection) infoURL.openConnection();
        HttpURLConnection picConnection = (HttpURLConnection) picURL.openConnection();

        StringBuilder requestDataInfo = new StringBuilder();
        StringBuilder requestDataPic = new StringBuilder();



        switch(inputReq) { // This just encodes the URL information for the request in UTF-8
            case FORMULA:
                requestDataInfo.append(URLEncoder.encode(
                        String.format("/compound/formula/%s/property/%s/JSON", input, requestedProperties),
                        "UTF-8")
                );
                requestDataPic.append(URLEncoder.encode(
                        String.format("/compound/formula/%s/PNG", input),
                        "UTF-8")
                );
                break;
            case NAME:
                requestDataInfo.append(URLEncoder.encode(
                        String.format("/compound/name/%s/property/%s/JSON", input, requestedProperties),
                        "UTF-8")
                );
                requestDataPic.append(URLEncoder.encode(
                        String.format("/compound/name/%s/PNG", input),
                        "UTF-8")
                );
                break;
        }

        // Translates request into array of bytes
        byte[] requestDataBytes = requestDataInfo.toString().getBytes(StandardCharsets.UTF_8);
        byte[] requestPicBytes = requestDataPic.toString().getBytes(StandardCharsets.UTF_8);


         */

        switch(inputReq) {
            case FORMULA:
                infoURL = new URL(
                                String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug" +
                                        "/compound/formula/%s/property/%s/JSON", input, requestedProperties)
                );

                picURL = new URL(
                                String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug" +
                                        "/compound/formula/%s/PNG", input)
                );
                break;
            case NAME:
                infoURL = new URL(
                                String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug" +
                                        "/compound/name/%s/property/%s/JSON", input, requestedProperties)
                );

                picURL = new URL(
                                String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug" +
                                        "/compound/name/%s/PNG", input)
                );
                break;
        }




        /*
        // Fires POST request
        infoConnection.setDoOutput(true);
        picConnection.setDoOutput(true);
        */

        // This is weird, but is actually a Java function called "try-with-resources"
        // I think this is because BufferedReader needs to be closed, even if an exception is thrown
        // Thus, a try-with-resources block is needed


        /*

        // Generates POST Request for info data
        try(DataOutputStream writer = new DataOutputStream(infoConnection.getOutputStream())) {
            writer.write(requestDataBytes);
            writer.flush();
            writer.close();

            StringBuilder content;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(infoConnection.getInputStream()))) {
                String line;
                content = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            System.out.println(content.toString());
        } finally {
            infoConnection.disconnect();
        }
        picConnection.disconnect();

        */

    }

    public String readURLs() throws IOException, InterruptedException {
        /*
        This if() statement is needed because for some reason
        PubChem will always make you wait if you request a formula, but
        it will give you a listkey in the webpage it spits back.

        If you re-request with
        https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/listkey/[ListKey]/cids/TXT
        then you can get the CID and then re-request with that CID
         */


        StringBuilder sb = new StringBuilder();

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(infoURL.openStream()))) { // Opens the URL in open stream
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        }

        Pattern p = Pattern.compile("Your request is running"); // This only occurs if there is a "waiting" message
        Matcher m = p.matcher(sb.toString());
        StringBuilder test = new StringBuilder();

        if(m.find()) { // Checks if
            m = p.matcher(sb.toString());
            boolean tester = m.find();
            while(tester) {
                URL listKeyURL = null;
                Pattern listKeyCheck = Pattern.compile("[0-9]{19}"); // Matches listkey, which is always 19 digits long

                m = listKeyCheck.matcher(sb.toString());
                if (m.find()) {
                    String listKey = m.group(0);
                    listKeyURL = new URL(
                            String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/listkey/%s/cids/TXT", listKey)
                    );
                }

                // This runs the new URL to check if there's still a running request, and attempts to get the CID
                assert listKeyURL != null;
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(listKeyURL.openStream()))) {
                    String line;
                    test = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        test.append(line);
                        test.append(System.lineSeparator());
                    }
                }

                m = p.matcher(test);
                tester = m.find();
                Thread.sleep(500); // Limit number of requests


            }
            Pattern pcid = Pattern.compile("[0-9]+"); // Matches every cid in the list, but it's okay
            Matcher mcid = pcid.matcher(test);
            if (mcid.find()) {
                String firstCID = mcid.group(0);

                // Using this cid, reform URLs using CID

                infoURL = new URL(
                        String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/%s/property/%s/JSON",
                                firstCID, requestedProperties)
                );

                picURL = new URL(
                        String.format("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/%s/PNG",
                                firstCID)
                );
            }

            // Finally, run the new URL with the CID
            sb = new StringBuilder();
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(infoURL.openStream()))) { // Opens the URL in open stream
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
            }
        }






        BufferedImage image;
        try {
            image = ImageIO.read(picURL);
            ImageIO.write(image, "png", new File("tmp.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }



}
