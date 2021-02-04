import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics;



public class ResizeImage {
    private int width;
    private int height;
    
    public ResizeImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private BufferedImage genBackground(int w, int h) {
        BufferedImage image = new BufferedImage(w, h,
                                       BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < w; ++x)
            for (int y = 0; y < h; ++y)
                image.setRGB(x, y, 0);
        
        return image;
    }

    
    private BufferedImage from(String filename) throws IOException {
        return ImageIO.read(new File(filename));
    }

    public void resize_and_save(String from, String to) throws IOException, Exception {
        BufferedImage img = resize(from);
        String parts[] = to.split("\\.");
        String format = parts[parts.length - 1];
        if (!ImageIO.write(img, format, new File(to)))
            throw new Exception("Error in saving image.");
    }

    
    public BufferedImage resize(String filename) throws IOException {
        return resize(from(filename));
    }

    public BufferedImage resize(BufferedImage scImage) {
        int scW = scImage.getWidth(), scH = scImage.getHeight();
        
        if (scW > width || scH > height)
            scImage = resizeInnerImage(scImage);

        return centerInnerImage(scImage);
    }

    private BufferedImage resizeInnerImage(BufferedImage img) {
        int scW = img.getWidth(), scH = img.getHeight();
        
        float scale_factor = 1.0f;
        if ((scW - width) > (scH - height))
            scale_factor = (float)width / scW;
        else
            scale_factor = (float)height / scH;

        int w = (int)(scale_factor * scW), h = (int)(scale_factor * scH);
        Image imgTkit = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);

        BufferedImage scaledImage = genBackground(w, h);
        Graphics g = scaledImage.getGraphics();
        g.drawImage(imgTkit, 0, 0, null);
        return scaledImage;
    }

    private BufferedImage centerInnerImage(BufferedImage img) {
        BufferedImage bf = genBackground(width, height);

        int x0 = (int)(width/2 - img.getWidth()/2);
        int y0 = (int)(height/2 - img.getHeight()/2);
        int w = img.getWidth(), h = img.getHeight();

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                int pixel = img.getRGB(x, y);
                bf.setRGB(x0 + x, y0 + y, pixel);
            }
        }                   
        
        return bf;
    }

    public static void main(String args[]) {
        try {

            String from = args[0];
            String to = args[1];
            int w = Integer.parseInt(args[2]);
            int h = Integer.parseInt(args[3]);
            
            ResizeImage ri = new ResizeImage(w, h);
            ri.resize_and_save(from, to);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }   
}
