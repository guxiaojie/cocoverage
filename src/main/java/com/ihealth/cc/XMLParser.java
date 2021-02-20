package com.ihealth.cc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Iterator;

@Configuration
@EnableAutoConfiguration
public class XMLParser {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    public static float parser(String originName, File file){
        float percentage = 0;
        SAXReader reader = new SAXReader();
        if (originName.equals("coverage.xml")) {
            float lineRate = 0;
            try {
                Document document = reader.read(file);
                Element faultStore = document.getRootElement();
                Attribute coveredstatementsAttr = faultStore.attribute("line-rate");
                try {
                    lineRate = Float.parseFloat(coveredstatementsAttr.getValue());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                logger.info(e);
            }
            percentage = lineRate;
        } else {
            try {
                Document document = reader.read(file);
                Element faultStore = document.getRootElement();
                Iterator<Element> it = faultStore.elementIterator();
                while (it.hasNext()) {
                    Element fault = (Element) it.next();
                    Iterator<Element> itt = fault.elementIterator();
                    while (itt.hasNext()) {
                        Element projectChild = (Element) itt.next();
                        Attribute statementsAttr = projectChild.attribute("statements");
                        Attribute coveredstatementsAttr = projectChild.attribute("coveredstatements");
                        int coveredstatements;
                        try {
                            coveredstatements = Integer.parseInt(coveredstatementsAttr.getValue());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            // check the example_clover.xml template, only read the child of a project node
                            // need an updated algorithm for other templates
                            break;
                        }
                        int statements = 0;
                        try {
                            statements = Integer.parseInt(statementsAttr.getValue());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            break;
                        }
                        if (statements != 0) {
                            percentage = (float) coveredstatements / (float) statements;
                        }
                    }
                }
            } catch (Exception e) {
                logger.info(e);
            }
        }
        return  percentage;
    }
}
