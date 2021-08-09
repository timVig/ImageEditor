package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Handles anything to do with reading or writing an image to a file.
 * As well as prompting for the inputs/output locations.
 */
public class FileIOHandler {

    /**
     * Prompts for the user to put in an image file.
     * @return -> int[][] array representation of image file
     * @throws IOException
     */
    public static int[][] promptForImageFile() throws IOException {
        JFileChooser getInput = new JFileChooser();
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter( "JPG & PNG Images", "jpg", "png");
        getInput.setFileFilter( fileFilter );
        int returnVal = getInput.showOpenDialog(null);
        if( returnVal == JFileChooser.APPROVE_OPTION ){
            File read = getInput.getSelectedFile();
            return readImage( read );
        } else return ImageManipulationHandler.initWhiteImage1000500();
    }

    /**
     * Reads in an image file for input.
     * @param input -> file to use for input.
     * @return -> int[][] array representation of image file
     * @throws IOException
     */
    public static int[][] readImage(File input) throws IOException {
        BufferedImage rImage = ImageIO.read( input );
        int[][] arr = new int[rImage.getWidth()][rImage.getHeight()];
        for( int i = 0; i < rImage.getWidth(); i++ )
            for (int j = 0; j < rImage.getHeight(); j++)
                arr[i][j] = rImage.getRGB( i, j );
        return arr;
    }

    /**
     * Write an image to a file from an input int[][] array.
     * @param outArr -> array to write to output
     * @param frame -> frame to prompt for output on.
     * @throws IOException
     */
    public static void writeImage( int[][] outArr, JFrame frame ) throws IOException {
        BufferedImage writeImage = new BufferedImage( outArr.length, outArr[0].length, BufferedImage.TYPE_INT_RGB );
        for( int i = 0; i < outArr.length; i++ )
            for( int j = 0; j < outArr[0].length; j++ )
                writeImage.setRGB( i, j, outArr[i][j] );
        JFileChooser output = new JFileChooser();
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter( "JPG & PNG Images", "jpg", "png");
        output.setFileFilter( fileFilter );
        output.setDialogTitle("Save this file as: ");

        int out = output.showSaveDialog( frame );
        if( out == JFileChooser.APPROVE_OPTION ){
            File outFile = output.getSelectedFile();
            ImageIO.write( writeImage, "png", outFile );
        }
    }

}
