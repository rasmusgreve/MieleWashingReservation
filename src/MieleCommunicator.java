import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MieleCommunicator {

	private final String ip;
	private String basicAuth;
	
	public MieleCommunicator(String ip) {
		this.ip = ip;
	}
	
	public void setCredentials(String username, String password){
		String userpass = username + ":" + password;
		basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
	}
	
	public boolean testCredentials(){
		try{
			getPage("");
			return true;
		}
		catch (IOException e){
			return false;
		}
	}
	
	public Collection<Reservation> getReservations() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException{
		final ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		parsePageButtons("MineReservationer", new ButtonParser() {
			@Override
			public void parse(String btnTxt) {
				reservations.add(Reservation.fromCResDelete(btnTxt));
			}
		});
		return reservations;
	}
	
	public Collection<Reservation> getOpenSlots(final GregorianCalendar date) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException{
		String urlDate = date.get(GregorianCalendar.YEAR) + "-" + date.get(GregorianCalendar.MONTH) + "-" + date.get(GregorianCalendar.DAY_OF_MONTH);
		final ArrayList<Reservation> slots = new ArrayList<Reservation>();
		parsePageButtons("ReserverTid?date=" + urlDate, new ButtonParser() {
			@Override
			public void parse(String btnTxt) {
				slots.add(Reservation.fromCResCreate(btnTxt, date));
			}
		});
		return slots;
	}
	
	private interface ButtonParser{
		void parse(String btnTxt);
	}
	
	private void parsePageButtons(String page, ButtonParser parser)throws IOException, SAXException, ParserConfigurationException, XPathExpressionException{
		Document doc = getPageDocument(page);
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//table/tbody/tr/td/a[@onclick]");
		
		NodeList res = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for(int i = 0; i < res.getLength();i++){
			Node n = res.item(i);
			String onclick = n.getAttributes().getNamedItem("onclick").getTextContent();
			parser.parse(onclick);
		}
	}
	
	public boolean deleteReservation(Reservation toDelete){
		String page = "ReserverTur?slet=1&lg=0&ly=" + toDelete.getUnknownId() + "&group=" + toDelete.getGroupId();
		page += "&date=" + toDelete.getYMDDate();
		page += "&time=" + toDelete.getPeriod().getPeriodString();
		
		System.out.println(page);
		
		return false;
	}
	
	
	public static String toString(Document doc) {
	    try {
	        StringWriter sw = new StringWriter();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        return sw.toString();
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
	}
	/**/
	
	private Document getPageDocument(String path) throws IOException, SAXException, ParserConfigurationException{
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setTranslateSpecialEntities(true);
		props.setAdvancedXmlEscape(true);
		TagNode tagNode = cleaner.clean(getPage(path),"iso-8859-1");
		return new DomSerializer(props, false).createDOM(tagNode);
	}
	
	private InputStream getPage(String path) throws IOException{
		URL url = new URL("http://" + ip + "/" + path);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty ("Authorization", basicAuth);
		return urlConnection.getInputStream();
	}

}
