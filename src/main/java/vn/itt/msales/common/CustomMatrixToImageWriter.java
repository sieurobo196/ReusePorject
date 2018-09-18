/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.itt.msales.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.google.zxing.common.BitMatrix;

/**
 * Writes a {@link BitMatrix} to {@link BufferedImage},
 * file or stream. Provided here instead of core since it depends on
 * Java SE libraries.
 *
 * @author Sean Owen
 */
public final class CustomMatrixToImageWriter {

  private static final CustomMatrixToImageConfig DEFAULT_CONFIG = new CustomMatrixToImageConfig();

  private CustomMatrixToImageWriter() {}

  /**
   * Renders a {@link BitMatrix} as an image, where "false" bits are rendered
   * as white, and "true" bits are rendered as black.
   */
  public static BufferedImage toBufferedImage(BitMatrix matrix) {
	  
    return toBufferedImage(matrix,DEFAULT_CONFIG);
  }

  /**
   * As {@link #toBufferedImage(BitMatrix)}, but allows customization of the output.
   */
  public static BufferedImage toBufferedImage(BitMatrix matrix, CustomMatrixToImageConfig config) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
//    Color color=new Color(191,159,98);
//    int onColor = color.getRGB();
    int offColor = config.getPixelOffColor();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : offColor);
      }
    }
    return image;
  }

  /**
   * Writes a {@link BitMatrix} to a file.
   *
   * @see #toBufferedImage(BitMatrix)
   */
  public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
    writeToFile(matrix, format, file, DEFAULT_CONFIG);
  }

  /**
   * As {@link #writeToFile(BitMatrix, String, File)}, but allows customization of the output.
   */
  public static void writeToFile(BitMatrix matrix, String format, File file, CustomMatrixToImageConfig config) 
      throws IOException {  
    BufferedImage image = toBufferedImage(matrix, config);
    if (!ImageIO.write(image, format, file)) {
      throw new IOException("Could not write an image of format " + format + " to " + file);
    }
  }

  /**
   * Writes a {@link BitMatrix} to a stream.
   *
   * @see #toBufferedImage(BitMatrix)
   */
  public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
    writeToStream(matrix, format, stream, DEFAULT_CONFIG);
  }

  /**
   * As {@link #writeToStream(BitMatrix, String, OutputStream)}, but allows customization of the output.
   */
  public static void writeToStream(BitMatrix matrix, String format, OutputStream stream, CustomMatrixToImageConfig config) 
      throws IOException {  
    BufferedImage image = toBufferedImage(matrix, config);
    if (!ImageIO.write(image, format, stream)) {
      throw new IOException("Could not write an image of format " + format);
    }
  }

}
