package bordomor.odtu.sk.apptest;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class TestClass 
{
	public static void main(String[] args) throws Exception
	{
		String clientId = "be11cd073d22a85bb7ffa1c8b94957c8";
		String clientSecret = "22fc81e2bea0649c45a978cda326829c";
		
		HttpResponse<String> response = Unirest.get("https://api.test.isbank.com.tr/api/sandbox-isbank/v1/accounts/%7Baccount-id%7D/transactions/%7Btransaction-id%7D")
				  .header("X-Isbank-Client-Id", clientId)
				  .header("X-Isbank-Client-Secret", clientSecret)
				  .header("Authorization", "Bearer REPLACE_BEARER_TOKEN")
				  .asString();
		System.out.println(response.getBody());
		
		/*File[] files = new File[] 
		{
				new File("/home/oguzaykun/Desktop/Rapor/on_yazi.pdf"),
				new File("/home/oguzaykun/Desktop/Rapor/a_blok_ozet.pdf"),
				new File("/home/oguzaykun/Desktop/Rapor/b_blok_ozet.pdf"),
				new File("/home/oguzaykun/Desktop/Rapor/c_blok_ozet.pdf"),
				new File("/home/oguzaykun/Desktop/Rapor/d_blok_ozet.pdf"),
				new File("/home/oguzaykun/Desktop/Rapor/e_blok_ozet.pdf")
		};
		
		String mergedFileName = "/home/oguzaykun/Desktop/son_ozet.pdf";
		
		try 
		{
	        PDFMergerUtility pdfmerger = new PDFMergerUtility();
	        int i = 0;
	        
	        for (File file : files) 
	        {
	            PDDocument document = Loader.loadPDF(file);
	            pdfmerger.setDestinationFileName(mergedFileName);
	            pdfmerger.addSource(file);
	            pdfmerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
	            document.close();
	            
	            System.out.println("Step " + (i + 1) + " done");
	            i++;
	        }
	    } catch (IOException e) {
	        System.out.println("Error to merge files. Error: " + e.getMessage());
	    }*/
	}
}