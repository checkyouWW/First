package com.ztesoft.crmpub.bpm.util;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

/**
 * 
 * <pre>
 * Title:类中文名称
 * Description: 格式化XML字符串
 * </pre>
 * @author caozj  cao.zhijun3@zte.com.cn
 * @date Dec 10, 2014 9:40:10 AM
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class XmlStrPraseUtil {

	
	/**
	 * 
	 * @Title: getXmlRootElement 
	 * @author caozj  cao.zhijun3@zte.com.cn
	 * @date Dec 10, 2014 2:11:27 PM
	 * @Description: 读取文件XML文档 
	 * @param xmlfile
	 * @return
	 * @throws Exception     
	 * @throws
	 */
	public static Element getXmlRootElement(File xmlfile) throws Exception{
		
		Document doc = null;
		SAXReader reader = new SAXReader(); 
		doc = reader.read(xmlfile); 
		//doc = DocumentHelper.parseText(xml); // 将字符串转为XML
        Element rootElt = doc.getRootElement(); // 获取根节点
        //System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称
       
		return rootElt;
	}
	
	
	/**
	 * 
	 * @Title: getXmlNodeByName 
	 * @author caozj  cao.zhijun3@zte.com.cn
	 * @date Dec 10, 2014 11:04:15 AM
	 * @Description: 方法功能描述 读取字符串XML文档 返回根节点
	 * @param xmlstr
	 * @return
	 * @throws Exception     
	 * @throws
	 */
	public static Element getXmlRootElement(String xmlstr) throws Exception{
		Document doc = null;
		SAXReader reader = new SAXReader(); 
		
		doc = DocumentHelper.parseText(xmlstr); // 将字符串转为XML
        Element rootElt = doc.getRootElement(); // 获取根节点
        
		return rootElt;
	}
	
	
	/**
	 * 
	 * @Title: getElementAttr 
	 * @author caozj  cao.zhijun3@zte.com.cn
	 * @date Dec 10, 2014 11:33:05 AM
	 * @Description: 去节点属性 
	 * @param element
	 * @return     
	 * @throws
	 */
	public static Map getElementAttrs(Element element){
		Map result = new HashMap();
		List attrList = element.attributes();
		for(int i=0;i<attrList.size();i++){
			Attribute item = (Attribute)attrList.get(i);
			result.put(item.getName(), item.getValue());
		}
		return result ;
	}
	
	/**
	 * 
	 * @Title: getElementAttrValue 
	 * @author caozj  cao.zhijun3@zte.com.cn
	 * @date Dec 10, 2014 11:43:55 AM
	 * @Description: 取节点属性
	 * @param element
	 * @param attrName
	 * @return     
	 * @throws
	 */
	public static String getElementAttrValue(Element element,String attrName){
		String result = "";
		List attrList = element.attributes();
		for(int i=0;i<attrList.size();i++){
			Attribute item = (Attribute)attrList.get(i);
			if(item.getName().equals(attrName)){
				result = item.getValue();
				break ;
			}
		}
		return result ;
	}
	/**
	 * 
	 * @Title: getElementByName 
	 * @author caozj  cao.zhijun3@zte.com.cn
	 * @date Dec 10, 2014 1:52:06 PM
	 * @Description: 方法功能描述 根据节点名 取下级
	 * @param element
	 * @param name
	 * @return     
	 * @throws
	 */
	public static Element getElementByName(Element element,String name){
		
		return element.element(name);
	}
	
	/**
	 * 
	 * @Title: getElementsByName 
	 * @author caozj  cao.zhijun3@zte.com.cn
	 * @date Dec 10, 2014 1:59:38 PM
	 * @Description: 取下级节点 
	 * @param element
	 * @param name
	 * @return     
	 * @throws
	 */
	public static List<Element> getElementsByName(Element element,String name){
		
		return element.elements(new QName(name,
	            element.getNamespace()));
	}
	
	
	public static void main(String[] args){
		
		File file = new File("E:/jxWorkspace/xml.txt");
		System.out.println(file.getAbsolutePath());
		
		try {
			Element rootE = XmlStrPraseUtil.getXmlRootElement(file);
			Element p = XmlStrPraseUtil.getElementByName(rootE, "process");
			
	
			List<Element> list = XmlStrPraseUtil.getElementsByName(p,"userTask");
			for(Element e:list){
				
				System.out.println(XmlStrPraseUtil.getElementAttrValue(e, "id"));
				System.out.println(XmlStrPraseUtil.getElementAttrValue(e, "name"));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
}
