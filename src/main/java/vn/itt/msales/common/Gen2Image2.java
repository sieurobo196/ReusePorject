package vn.itt.msales.common;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import net.glxn.qrgen.QRCode;

public class Gen2Image2 {

	private BufferedImage process(HttpServletRequest request, BufferedImage old, String text) throws IOException {
        int w = old.getWidth();
        int h = old.getHeight()+10;
        BufferedImage img = new BufferedImage(
            w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, w, 10);
        g2d.drawImage(old, 0, 10,null);
        
        BufferedImage image = ImageIO.read(new File(request.getRealPath("") + "/images/slogan.jpg"));
        g2d.drawImage(image,30, 10, null );
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        String s = text;
        FontMetrics fm = g2d.getFontMetrics();
        int x = img.getWidth() - fm.stringWidth(s) - 30;
        int y = img.getHeight() - 15;//fm.getHeight();
        g2d.drawString(s, x, y);
        g2d.dispose();
        // resize image
        /*
        BufferedImage resizedImage = new BufferedImage(240, 260, BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = resizedImage.createGraphics();
    	g.drawImage(img, 0, 0, 236, 255, null); // 8.33x9.0
    	g.dispose();
        return resizedImage;
        */
        return img;
    }

	public static void Gen2Image(HttpServletRequest request, String QRPath, String maDBH, String quanHuyen) {
	 try{
		  // Open the file that is the first 
		  // command line parameter
		  //FileInputStream fstream = new FileInputStream(QRPath + "/test.txt");
		  // Get the object of DataInputStream
		  //DataInputStream in = new DataInputStream(fstream);
		  //BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  //String strLine;
		  //Read File Line By Line
		  //while ((strLine = br.readLine()) != null){
		 //	for (int i =0; i < posSelected.size(); i++){
		 	//	strLine = posSelected.get(i);
		  // Print the content on the console
		 	/*
		 	File path = new File(QRPath);
			if(!path.exists()){
				path.mkdir();
				path.setExecutable(true);
				path.setWritable(true);
				path.setReadable(true);
				System.out.println("New directory "+ path +" is created !");
			}			
			*/
		  ByteArrayOutputStream out = QRCode.from("MOBIFONE TT KHU VUC II\nshop_id:" + maDBH).withSize(241, 241).stream();
	        // .to(ImageType.PNG).stream();
	
			//FileOutputStream fout = new FileOutputStream(new File(QRPath + "/temp.JPG"));//(new File("/QRCode/" + strLine + ".JPG"));
			FileOutputStream fout = new FileOutputStream(new File(QRPath + "/" + maDBH + ".PNG"));
			
			fout.write(out.toByteArray());
			
			fout.flush();
			fout.close();  
		  
			
			
			//BufferedImage image = ImageIO.read(new File(QRPath + "/temp.JPG"));// + strLine + ".JPG"));
			 BufferedImage image = ImageIO.read(new File(QRPath + "/" + maDBH + ".PNG"));
			
			Gen2Image2 a = new Gen2Image2();
			//String CN = "CN5";
			//String Dist = "Q.07";
			//image = a.process(image, strLine + " (" + Dist + ")");
			image = a.process(request, image, maDBH + " (" + quanHuyen + ")");
			//ImageIO.write(image, "png", new File(QRPath + "/" + CN + "/" + Dist + "/" + strLine + ".png"));
			ImageIO.write(image, "PNG", new File(QRPath + "/" + maDBH + ".PNG"));
		  
			//System.out.println (strLine);
		  //Close the input stream
		  //in.close();
		  System.out.println("* DONE GENERATING QRCODE FOR DBH " + maDBH + " " + quanHuyen + " *");
		 }catch (Exception e){//Catch exception if any
		  System.err.println("Error: " + e.getMessage());
		 }	
	 }

}
