package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Handles all image manipulation, whether it be rotations, resetting, applying filters, brightness, etc.
 * Most functions take in the int[][] array and edit it within the class, returning void.
 * Some functions need other parameters passed along to them, done in the Main Action events.
 */
public class ImageManipulationHandler {

    /**
     * Initializes the original 1000x500 grid before an image is selected
     * @return -> an int[1000][500] array filled with white pixels.
     */
    public static int[][] initWhiteImage1000500(){
        int[][] init = new int[1000][500];
        for( int i = 0; i < 1000; i++ )
            for( int j = 0; j < 500; j++ )
                init[i][j] = 255 << 16 | 255 << 8 | 255;
        return init;
    }

    /**
     * Converts an array to an Image type
     * @param arr -> array to convert
     * @return -> image conversion of arr
     */
    public static Image arrayToImage( int[][] arr ){
        BufferedImage wImage = new BufferedImage( arr.length, arr[0].length, BufferedImage.TYPE_INT_RGB );
        for( int i = 0; i < arr.length; i++ )
            for( int j = 0; j < arr[0].length; j++ )
                wImage.setRGB( i, j, arr[i][j] );
        return wImage;
    }

    /**
     * Rotates an image a specified # of degrees
     * @param mode -> clock/counterclockwise
     * @param degrees -> 90, 180, and 270 are valid values
     * @param matrix -> an NxN matrix
     */
    public static void rotateImage( int mode, int degrees, int[][] matrix ){
        if( matrix.length != matrix[0].length ) return;
        if( mode == 1 ) { //rotate left
            if( degrees == 90 || degrees == 180 || degrees == 270 ) rotateLeft90(matrix);
            if( degrees == 180 || degrees == 270 ) rotateLeft90(matrix);
            if( degrees == 270 ) rotateLeft90(matrix);
        } else if( mode == 2 ){ //rotate right
            flipHorizontal(matrix);
            if( degrees == 90 || degrees == 180 || degrees == 270 ) rotateLeft90(matrix);
            if( degrees == 180 || degrees == 270 ) rotateLeft90(matrix);
            if( degrees == 270 ) rotateLeft90(matrix);
            flipHorizontal(matrix);
        }
    }

    /**
     * Rotates an NxN matrix in place 90 degrees clockwise, does not work for MxN matrices where N != M.
     * @param matrix -> An NxN matrix
     */
    public static void rotateLeft90( int[][] matrix ){
        int t; int temp;
        for( int i = 0; i < matrix.length/2; i++ ){
            for( int j = 0; j < matrix[i].length/2 + (matrix.length%2); j++ ){
                temp = matrix[j][matrix.length-i-1];
                matrix[j][matrix.length-i-1] = matrix[i][j]; //swap upper
                t = matrix[matrix.length-i-1][matrix.length-j-1];
                matrix[matrix.length-i-1][matrix.length-j-1] = temp; //swap right side
                temp = t;
                t = matrix[matrix.length-j-1][i];
                matrix[matrix.length-j-1][i] = temp; //swap below
                temp = t;
                matrix[i][j] = temp;
            }
        }
    }

    /**
     * Flips an MxN matrix vertically, so it is upside down.
     * @param matrix -> An MxN matrix
     */
    public static void flipVertical( int[][] matrix ){
        int end = matrix[0].length - 1;
        for( int start = 0; start < (matrix[0].length / 2) - 1; start++ ){
            for( int startCol = 0; startCol < matrix.length - 1; startCol++ ){
                int temp = matrix[startCol][start];
                matrix[startCol][start] = matrix[startCol][end];
                matrix[startCol][end] = temp;
            } end--;
        }
    }

    /**
     * Flips an MxN matrix horizontally, essentially backwards
     * @param matrix -> An MxN matrix
     */
    public static void flipHorizontal( int[][] matrix ){
        int end = matrix.length - 1;
        for( int start = 0; start < (matrix.length / 2) - 1; start++ ){
            for( int startRow = 0; startRow < matrix[0].length; startRow++ ){
                int temp = matrix[start][startRow];
                matrix[start][startRow] = matrix[end][startRow];
                matrix[end][startRow] = temp;
            } end--;
        }
    }

    /**
     * Edits the brightness of an MxN Matrix
     * @param matrix -> matrix to edit
     * @param sliderImage -> preview to display
     * @param imageWidth -> width of matrix
     * @param imageHeight -> height of matrix
     * @param brightenFactor -> how much to brighten (0.0 - 1.00)
     */
    public static void brightnessEdit( int[][] matrix, int[][] sliderImage, int imageWidth, int imageHeight,
                                       float brightenFactor ){
        float brighten = 1 + brightenFactor;
        BufferedImage bufferImage = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < sliderImage.length; i++)
            for (int j = 0; j < sliderImage[0].length; j++)
                bufferImage.setRGB(i, j, sliderImage[i][j]);

        RescaleOp op = new RescaleOp( brighten, 0, null );
        bufferImage = op.filter( bufferImage, bufferImage );
        for( int i = 0; i < imageWidth; i++ )
            for (int j = 0; j < imageHeight; j++)
                matrix[i][j] = bufferImage.getRGB(i, j);
    }

    /**
     * Add a textbox at a specific height
     * @param matrix -> An MxN matrix
     * @param imageWidth -> width of matrix
     * @param imageHeight -> height of matrix
     * @param x -> x to put box
     * @param y -> y to put box
     * @param fontSize -> font size
     * @param fontType -> font type
     * @param text -> text to put
     */
    public static void addTextbox( int[][] matrix, int imageWidth, int imageHeight, int x, int y, int fontSize,
                                   String fontType, String text ) {
        BufferedImage bufferImage = new BufferedImage(matrix.length, matrix[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                bufferImage.setRGB(i, j, matrix[i][j]);

        Graphics2D graphics = bufferImage.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.setFont( new Font( fontType, Font.BOLD, fontSize ));
        graphics.drawString( text, x, y );

        for( int i = 0; i < imageWidth; i++ )
            for (int j = 0; j < imageHeight; j++)
                matrix[i][j] = bufferImage.getRGB(i, j);
    }

    /**
     * Resets the image as if it weren't edited
     * @param matrix -> current image
     * @param original -> original copy
     */
    public static void setOriginal( int[][] matrix, int[][] original ){
        for( int i = 0; i < matrix.length; i++ )
            for( int j = 0; j < matrix[0].length; j++ )
                matrix[i][j] = original[i][j];
    }

    /**
     * Create grayscale representation of am image as a matrix
     * @param matrix -> an MxN matrix
     */
    public static void greyScale( int[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ){
            for ( int j = 0; j < matrix[i].length; j++ ){
                int rgb = matrix[i][j];
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xff;
                int green = (rgb >> 8) & 0xff;
                int blue = rgb & 0xff;
                int greyscale = ( red + green + blue ) / 3;
                int correctGreyscale = (alpha << 24) | greyscale << 16 | greyscale << 8 | greyscale;
                matrix[i][j] = correctGreyscale;
            }
        }
    }

    /**
     * Create inverted representation of am image as a matrix
     * @param matrix -> an MxN matrix
     */
    public static void invert( int[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ){
            for ( int j = 0; j < matrix[i].length; j++ ){
                int rgb = matrix[i][j];
                int alpha = (rgb >> 24) & 0xff;
                int red = 255 - ((rgb >> 16) & 0xff);
                int green = 255 - ((rgb >> 8) & 0xff);
                int blue = 255 - (rgb & 0xff);
                int inverted = (alpha << 24) | red << 16 | green << 8 | blue;
                matrix[i][j] = inverted;
            }
        }
    }

    /**
     * Create redscale representation of am image as a matrix
     * @param matrix -> an MxN matrix
     */
    public static void redScale( int[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ){
            for ( int j = 0; j < matrix[i].length; j++ ){
                int rgb = matrix[i][j];
                int alpha = (rgb >> 24) & 0xff;
                int red = (rgb >> 16) & 0xff;
                matrix[i][j] = (alpha << 24) | red << 16;
            }
        }
    }

    /**
     * Create greenscale representation of am image as a matrix
     * @param matrix -> an MxN matrix
     */
    public static void greenScale( int[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ){
            for ( int j = 0; j < matrix[i].length; j++ ){
                int rgb = matrix[i][j];
                int alpha = (rgb >> 24) & 0xff;
                int green = (rgb >> 8) & 0xff;
                matrix[i][j] = (alpha << 24) | green << 8;
            }
        }
    }

    /**
     * Create bluescale representation of am image as a matrix
     * @param matrix -> an MxN matrix
     */
    public static void blueScale( int[][] matrix ) {
        for( int i = 0; i < matrix.length; i++ ){
            for ( int j = 0; j < matrix[i].length; j++ ){
                int rgb = matrix[i][j];
                int alpha = (rgb >> 24) & 0xff;
                int blue = rgb & 0xff;
                matrix[i][j] = (alpha << 24) | blue;
            }
        }
    }
}