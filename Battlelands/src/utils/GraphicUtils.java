package utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class GraphicUtils {
	
	/**
	 * This function takes an image and flips it at an angle
	 * @param image to flip
	 * @param angle between 0 and 2 * Pi
	 * @return returns the image to the same dimension but at the indicated angle
	 * 
	 * <br>
	 * <br>
	 * <h1 style="color:#FF5733";>Warning</h1>
	 * <br>
	 *   Affline transform only works with perfect squares. The following
	 *   code is used to take any rectangle image and rotate it correctly.
	 *   To do this it chooses a center point that is half the greater
	 *   length and tricks the library to think the image is a perfect
	 *   square, then it does the rotation and tells the library where
	 *   to find the correct top left point. The special cases in each
	 *   orientation happen when the extra image that doesn't exist is
	 *   either on the left or on top of the image being rotated. In
	 *   both cases the point is adjusted by the difference in the
	 *   longer side and the shorter side to get the point at the 
	 *   correct top left corner of the image. NOTE: the x and y
	 *   axes also rotate with the image so where width > height
	 *   the adjustments always happen on the y axis and where
	 *   the height > width the adjustments happen on the x axis.
	 */
	public static BufferedImage rotate(BufferedImage image, double angle)
	{

		
	  AffineTransform xform = new AffineTransform();

	  if (image.getWidth() > image.getHeight())
	  {
	    xform.setToTranslation(0.5 * image.getWidth(), 0.5 * image.getWidth());
	    xform.rotate(angle);

	    int diff = image.getWidth() - image.getHeight();

	    switch ((int)Math.toDegrees(angle))
	    {
	    case 90:
	      xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth() + diff);
	      break;
	    case 180:
	      xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth() + diff);
	      break;
	    default:
	      xform.translate(-0.5 * image.getWidth(), -0.5 * image.getWidth());
	      break;
	    }
	  }
	  else if (image.getHeight() > image.getWidth())
	  {
	    xform.setToTranslation(0.5 * image.getHeight(), 0.5 * image.getHeight());
	    xform.rotate(angle);

	    int diff = image.getHeight() - image.getWidth();

	    switch ((int)Math.toDegrees(angle))
	    {
	    case 180:
	      xform.translate(-0.5 * image.getHeight() + diff, -0.5 * image.getHeight());
	      break;
	    case 270:
	      xform.translate(-0.5 * image.getHeight() + diff, -0.5 * image.getHeight());
	      break;
	    default:
	      xform.translate(-0.5 * image.getHeight(), -0.5 * image.getHeight());
	      break;
	    }
	  }
	  else
	  {
	    xform.setToTranslation(0.5 * image.getWidth(), 0.5 * image.getHeight());
	    xform.rotate(angle);
	    xform.translate(-0.5 * image.getHeight(), -0.5 * image.getWidth());
	  }

	  AffineTransformOp op = new AffineTransformOp(xform, AffineTransformOp.TYPE_BILINEAR);

	  BufferedImage newImage =new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
	  return op.filter(image, newImage);
	}
}
