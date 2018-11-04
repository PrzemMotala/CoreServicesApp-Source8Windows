package com.przemekm.coreservicesapp.utilities;

import com.przemekm.coreservicesapp.controllers.MainWindow;
import com.przemekm.coreservicesapp.database.H2Database;
import com.przemekm.coreservicesapp.datamodel.Order;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLFileLoader implements FileLoader {
    /**
     * This method loads data from the XML file.
     * <p>
     * Each line of proper data is saved in a H2 database
     * with use of {@link H2Database#saveData(Order)} method.
     * If the file has missing tags inside {@code <request>} tag or the loaded data is in a wrong format,
     * an {@link IllegalArgumentException} is caught and a message is displayed.
     * <p>
     * It is assumed that the format of XML file is as follows:
     * <pre>
     * {@code
     *     <requests>
     *         <request>
     *             <clientId>String</clientId>
     *             <requestId>long</requestId>
     *             <name>String</name>
     *             <quantity>int</quantity>
     *             <price>BigDecimal</price>
     *         </request>
     *     </requests>
     * }
     * </pre>
     *
     * @param file the {@link File} to read from.
     * @return {@code true} if the file has at least one batch of data in proper format.
     * @see H2Database#saveData(Order)
     * @see DocumentBuilderFactory
     * @see Document
     */
    @Override
    public boolean load(File file) {
        boolean isFileNotEmpty = false;

        String[] data = new String[TAGS_LIST.size()];
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Couldn't parse the file!");
            e.printStackTrace();
        }

        NodeList nodeList = document.getElementsByTagName("request");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                StringBuilder dataBuilder = new StringBuilder();

                for (int j = 0; j < TAGS_LIST.size(); j++) {
                    try {
                        data[j] = element.getElementsByTagName(TAGS_LIST.get(j))
                                .item(0)
                                .getTextContent();
                    } catch (NullPointerException e) {
                        /*
                            Occurs when one of the tags in <request> tag is missing.
                         */
                        data[j] = "";
                    }
                    if (!(j == (TAGS_LIST.size() - 1))) {
                        dataBuilder.append(data[j]).append(",");
                    } else {
                        dataBuilder.append(data[j]);
                    }
                }

                try {
                    H2Database.getInstance()
                            .saveData(new Order(data));
                    isFileNotEmpty = true;
                } catch (IllegalArgumentException e) {
                    MainWindow.setTextToDisplay("Line \""
                            + dataBuilder.toString()
                            + "\" skipped - wrong format!"
                            + System.lineSeparator());
                }
            }
        }

        if (!isFileNotEmpty) {
            MainWindow.setTextToDisplay("No suitable lines found in XML file "
                    + file.getName() + "!"
                    + System.lineSeparator());
            return false;
        } else {
            MainWindow.setTextToDisplay("XML file "
                    + file.getName()
                    + " loaded successfully!" + System.lineSeparator());
            return true;
        }
    }
}
