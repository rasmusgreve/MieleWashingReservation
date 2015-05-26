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
	
	/**
	 * Set the credentials to use for communication
	 */
	public void setCredentials(String username, String password){
		String userpass = username + ":" + password;
		basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
	}
	
	/**
	 * Test that the credentials works (see {@link #setCredentials(String, String)})
	 * @return True if the credentials work
	 */
	public boolean testCredentials(){
		try{
			getPage("");
			return true;
		}
		catch (IOException e){
			return false;
		}
	}
	
	/**
	 * Get a collection of all the currently made reservations
	 */
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
	
	/**
	 * Get a collection of all available machine slots on a given date
	 * @param date The date to search for available machines on
	 * @return The collection of available slots
	 */
	public Collection<Reservation> getOpenSlots(final GregorianCalendar date) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException{
		String urlDate = date.get(GregorianCalendar.YEAR) + "-" + (date.get(GregorianCalendar.MONTH)+1) + "-" + date.get(GregorianCalendar.DAY_OF_MONTH);
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
	
	/**
	 * Delete a reservation
	 * @param toDelete The reservation to delete. The reservation should be obtained through the {@link #getReservations()} method
	 * @return True if the deletion was successful
	 */
	public boolean deleteReservation(Reservation toDelete) throws IOException, SAXException, ParserConfigurationException{
		//?slet=1&lg=0&ly='+arg6+'&date='+arg5+'&time='+arg7+'&group='+arg3
		String page = "ReserverTur?slet=1&lg=0&ly=" + toDelete.getUnknownId();
		page += "&date=" + toDelete.getYMDDate();
		page += "&time=" + toDelete.getPeriod().getPeriodString();
		page += "&group=" + toDelete.getGroupId();
		///ReserverTur?slet=1&lg=0&ly='+arg6+'&date='+arg5+'&time='+arg7+'&group='+arg3
		Document doc = getPageDocument(page);
		System.out.println(toString(doc));
		
		return false;
	}
	
	/**
	 * Make a reservation
	 * @param toCreate The reservation to create. The reservation should be obtained through the {@link #getOpenSlots(GregorianCalendar)} method
	 * @return
	 */
	public boolean makeReservation(Reservation toCreate){
		
		return false;
	}
	
	/*
	 * Print a XML Document in full. Used for debugging
	 */
	private static String toString(Document doc) {
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
		System.out.println("Getting: " + url);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Referer", "http:/" + ip + "/MineReservationer&lg=0&ly=9208");
		urlConnection.setRequestProperty ("Authorization", basicAuth);
		
		urlConnection.setRequestProperty ("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		urlConnection.setRequestProperty ("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		
		return urlConnection.getInputStream();
	}

}
