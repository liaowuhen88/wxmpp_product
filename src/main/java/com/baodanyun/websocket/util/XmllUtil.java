package com.baodanyun.websocket.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Created by liaowuhen on 2016/11/9.
 */
public class XmllUtil {
    public static List<Element> xmlElements(String xmlDoc) {
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();
        try {
            //通过输入源构造一个Document
            Document doc = sb.build(source);
            //取的根元素
            Element root = doc.getRootElement();

            //System.out.println(root.getName());//输出根元素的名称（测试）
            //得到根元素所有子元素的集合
            List<Element> jiedian = root.getChildren();

            return jiedian;
            //获得XML中的命名空间（XML中未定义可不写）
           /* Namespace ns = root.getNamespace();
            Element et = null;
            for(int i=0;i<jiedian.size();i++){
                et = (Element) jiedian.get(i);//循环依次得到子元素

                System.out.println(et.getChild("users_id",ns).getText());
                System.out.println(et.getChild("users_address",ns).getText());
            }*/

           /* et = (Element) jiedian.get(0);
            List zjiedian = et.getChildren();
            for(int j=0;j<zjiedian.size();j++){
                Element xet = (Element) zjiedian.get(j);
                System.out.println(xet.getName());
            }*/
        } catch (JDOMException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return null;
    }

    public static Element xmlElementRoot(String xmlDoc) throws JDOMException, IOException {
        xmlDoc = stripNonValidXMLCharacters(xmlDoc);
        //创建一个新的字符串
        StringReader read = new StringReader(xmlDoc);
        //创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        //创建一个新的SAXBuilder
        SAXBuilder sb = new SAXBuilder();

        //通过输入源构造一个Document
        Document doc = sb.build(source);
        //取的根元素
        Element root = doc.getRootElement();

        return root;

    }

    public static String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in)))
            return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
            // here; it should not happen.
            if ((current == 0x9) || (current == 0xA) || (current == 0xD)
                    || ((current >= 0x20) && (current <= 0xD7FF))
                    || ((current >= 0xE000) && (current <= 0xFFFD))
                    || ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }

    /*public static void main(String[] args){
        XmllUtil doc = new XmllUtil();
        String xml = "<?xml version=\"1.0\" encoding=\"gb2312\"?>"+
                "<Result xmlns=\"http://www.fiorano.com/fesb/activity/DBQueryOnInput2/Out\">"+
                "<row resultcount=\"1\">"+
                "<users_id>1001     </users_id>"+
                "<users_name>wangwei   </users_name>"+
                "<users_group>80        </users_group>"+
                "<users_address>1001号   </users_address>"+
                "</row>"+
                "<row resultcount=\"1\">"+
                "<users_id>1002     </users_id>"+
                "<users_name>wangwei   </users_name>"+
                "<users_group>80        </users_group>"+
                "<users_address>1002号   </users_address>"+
                "</row>"+
                "</Result>";
        doc.xmlElements(xml);
    }
*/
}
