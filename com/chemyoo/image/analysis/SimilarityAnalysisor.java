package com.chemyoo.image.analysis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * ͼƬ���ƶȷ���
 * @author Administrator
 */
public class SimilarityAnalysisor {
	
	private static final String IMGAGE_EXT = "jpg,jpeg,png,bmp,gif";
	
	public static double getSimilarity(File imgFile1, File imgFile2){
		if(isImageFile(imgFile1) && isImageFile(imgFile2)){
			// ��ȡ����ͼ�ĺ�������
			
			Image picImage1 = file2Image(imgFile1);
			Image picImage2 = file2Image(imgFile2);
		    int hammingDistance = getHammingDistance(
		    		getPixelsWithHanming(picImage1), 
		    		getPixelsWithHanming(picImage2));
		    
		    // ���ͼƬռ�õ��ڴ�
		    picImage1.flush();
		    picImage2.flush();
		    
		    // ͨ����������������ƶȣ�ȡֵ��Χ [0.0, 1.0]
		    return calSimilarity(hammingDistance);
		}
		return 0D;
	}
	
	/**
	 * �ļ�����תImage����
	 * @param image
	 * @return
	 */
	private static Image file2Image(File image){
		try {
			return ImageIO.read(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static int[] getPixelsWithHanming(Image image){
		// ת�����Ҷ�
		image = toGrayscale(image);
	    // ��С��32x32������ͼ
	    image = scale(image);
	    // ��ȡ�Ҷ���������
	    int[] pixels = getPixels(image);
	    //�ͷ�ͼƬ����
	    image.flush();
	    // ��ȡƽ���Ҷ���ɫ
	    int averageColor = getAverageOfPixelArray(pixels);
	    // ��ȡ�Ҷ����صıȽ����飨��ͼ��ָ�����У�
	    return getPixelDeviateWeightsArray(pixels, averageColor);
	}
	
	/**(
	 * �ж��Ƿ���ͼƬ�ļ�
	 * @param f
	 * @return
	 */
	private static boolean isImageFile(File f){
		return f.isFile() && IMGAGE_EXT.contains(getFileExt(f));
	}
	
	private static String getFileExt(File f){
		if(f != null){
			String fileName = f.getName();
			int index = fileName.lastIndexOf('.') + 1;
			if(index > 0){
				return fileName.substring(index);
			}
		}
		return null;
	}
	
	private SimilarityAnalysisor() {
	}

	//	 ������Image����ͼ��ת��ΪBufferedImage���ͣ������������
	private static BufferedImage convertToBufferedFrom(Image srcImage) {
		BufferedImage bufferedImage = new BufferedImage(
				srcImage.getWidth(null), srcImage.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufferedImage.createGraphics();
		g.drawImage(srcImage, null, null);
		g.dispose();
		return bufferedImage;
	}

	//	 ת�����Ҷ�ͼ
	private static BufferedImage toGrayscale(Image image) {
		BufferedImage sourceBuffered = convertToBufferedFrom(image);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage grayBuffered = op.filter(sourceBuffered, null);
		return grayBuffered;
	}

	//	 ������32x32��������ͼ
	private static Image scale(Image image) {
		image = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
		return image;
	}

	//	 ��ȡ��������
	private static int[] getPixels(Image image) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int[] pixels = convertToBufferedFrom(image).getRGB(0, 0, width, height,
				null, 0, width);
		return pixels;
	}

	//	 ��ȡ�Ҷ�ͼ��ƽ��������ɫֵ
	private static int getAverageOfPixelArray(int[] pixels) {
		Color color;
		long sumRed = 0;
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			sumRed += color.getRed();
		}
		int averageRed = (int) (sumRed / pixels.length);
		return averageRed;
	}

	//	 ��ȡ�Ҷ�ͼ�����رȽ����飨ƽ��ֵ����
	private static int[] getPixelDeviateWeightsArray(int[] pixels,
			final int averageColor) {
		Color color;
		int[] dest = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			color = new Color(pixels[i], true);
			dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
		}
		return dest;
	}

	//	 ��ȡ��������ͼ��ƽ�����رȽ�����ĺ������루����Խ�����Խ��
	private static int getHammingDistance(int[] a, int[] b) {
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] == b[i] ? 0 : 1;
		}
		return sum;
	}

	//	 ͨ����������������ƶ�
	private static double calSimilarity(int hammingDistance) {
		int length = 32 * 32;
		double similarity = (length - hammingDistance) / (double) length;

		// ʹ��ָ�����ߵ������ƶȽ��
		similarity = Math.pow(similarity, 2);
		return similarity;
	}
	
	public static void main(String[] args) {
		File f1 = new File("F:/picture/images/2345.jpg");
		File f2 = new File("F:/picture/images/1529244174585.jpg");
		System.err.println(SimilarityAnalysisor.getSimilarity(f1, f2));
	}
}
