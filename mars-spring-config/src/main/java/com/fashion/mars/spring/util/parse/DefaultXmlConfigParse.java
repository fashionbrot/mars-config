
package com.fashion.mars.spring.util.parse;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.*;


/**
 * Just support xml config like this
 */
public class DefaultXmlConfigParse implements ConfigParse  {

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    @Override
    public Properties parse(String configText) {
        Properties properties = new Properties();
        try {
            Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(configText.getBytes("UTF-8")));
            Element root = document.getDocumentElement();
            Map<String, Object> map = new LinkedHashMap<String, Object>(8);
            recursionXmlToMap(map, root);
            mapToProperties("", properties, map);
        } catch (Exception e) {
            throw new ConfigParseException(e);
        }
        return properties;
    }

    @Override
    public ConfigTypeEnum configType() {
        return ConfigTypeEnum.XML;
    }

    private void recursionXmlToMap(Map<String, Object> outMap, Element element) {
        NodeList nodeList = element.getChildNodes();
        String name = element.getNodeName();
        if (nodeList.getLength() == 1 && !nodeList.item(0).hasChildNodes()) {
            addData(outMap, name, element.getTextContent());
        } else {
            Map<String, Object> innerMap = new LinkedHashMap<String, Object>(1);
            int length = nodeList.getLength();
            for (int i = 0; i < length; i ++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tElement = (Element) node;
                    recursionXmlToMap(innerMap, tElement);
                }
            }
            addData(outMap, name, innerMap);
        }
    }

    private void addData(Map<String, Object> map, String key, Object data) {
        if (map.containsKey(key)) {
            if (map.get(key) instanceof List) {
                ((List) map.get(key)).add(data);
            } else {
                List<Object> list = new LinkedList<Object>();
                list.add(map.get(key));
                map.put(key, list);
            }
        } else {
            map.put(key, data);
        }
    }

    private void mapToProperties(String prefixName, Properties properties, Object data) {
        if (data instanceof List) {
            List list = (List) data;
            for (int i = 0; i < list.size(); i ++) {
                int lastIndex = prefixName.lastIndexOf('.');
                String preName = prefixName.substring(0, lastIndex);
                String lastName = prefixName.substring(lastIndex);
                mapToProperties(preName + "[" + i + "]", properties, list.get(i));
            }
        } else if (data instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) data;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String tmpPrefix = StringUtils.isEmpty(prefixName) ? entry.getKey() : prefixName  + "." + entry.getKey();
                mapToProperties(tmpPrefix, properties, entry.getValue());
            }
        } else {
            properties.setProperty(prefixName, String.valueOf(data));
        }
    }


}
