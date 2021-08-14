package com.company;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;

import static com.company.FileIOHandler.promptForImageFile;
import static com.company.ImageManipulationHandler.initWhiteImage1000500;

public class Main {
    private final static String[] filters = {"greyscale", "redscale", "greenscale", "bluescale", "invert", "reset" };
    private final static String[] rotationModes = { "left (NxN Image Only)", "right (NxN Image Only)" };
    private final static String[] rotationAngles = { "90", "180", "270" };
    private final static String[] flipOptions = { "horizontal", "vertical" };
    private final static String[] fontOptions = { "TimesNewRoman", "Calibri", "SansSerif" };
    private final static String[] fileIO = {"Load Image", "Save Image"};
    private static int rotateMode = 1;

    private static int[][] imageGrid = initWhiteImage1000500();
    private static int[][] originalImage = initWhiteImage1000500();
    private static int[][] sliderImage = initWhiteImage1000500();
    private static int imageWidth = 1000;
    private static int imageHeight = 500;
    private static JFrame frame;
    private static JComboBox<String> fonts;
    private static JTextField fontSize;
    private static JTextField textboxXCoor;
    private static JTextField textboxYCoor;
    private static JTextField textInput;
    private static JSlider brightSlider;

    /**
     * Makes one call to resetFrame, then lets mouse listeners do their thing until JFrame exits.
     * @param args -> nothing important.
     */
    public static void main(String[] args) {
        resetFrame();
    }

    /**
     * Resets the JFrame to a new state that is resized to the new image, does this by creating a new JFrame based
     * off the new image input.
     */
    public static void resetFrame(){
        if( frame != null ) frame.dispose();
        frame = new JFrame();
        frame.getContentPane().add( new ImagePanel() );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize( imageWidth + 60, (imageHeight + 60) * 2 );
        frame.setVisible( true );
        frame.setLayout(null);
        startUI();
        frame.repaint();
    }

    /**
     * Allows me to get the private image array.
     * @return -> the image array.
     */
    public static int[][] getImageArray(){ return imageGrid; }

    /**
     * Creates all the buttons, sliders, and fields needed for the UI
     * Does this by deriving spacing off current image size and laying it all out.
     */
    public static void startUI(){
        int offset; int count = 0;

        int rotateButtonSize = (imageWidth / rotationModes.length) - 20;
        int rotateSpacing = (imageWidth - (( imageWidth / rotateButtonSize ) * rotateButtonSize ))
                / (( imageWidth / rotateButtonSize ) - 1);

        int rotateAngleButtonSize = ( imageWidth / rotationAngles.length ) - 20;
        int rotateAngleSpacing = (imageWidth - (( imageWidth / rotateAngleButtonSize ) * rotateAngleButtonSize ))
                / (( imageWidth / rotateAngleButtonSize ) - 1);

        int flipButtonSize = ( imageWidth / flipOptions.length ) - 20;
        int flipSpacing = (imageWidth - (( imageWidth / flipButtonSize ) * flipButtonSize ))
                / (( imageWidth / flipButtonSize ) - 1);

        int filterButtonSize = ( imageWidth / 3 ) - 20;
        int filterSpacing = (imageWidth - (3 * filterButtonSize)) / (3 - 1);

        int textboxX = 20;
        int addTextButtonSize = (int) ( (double) imageWidth / 4.3 ) - 20;
        int selectionButtonSize = addTextButtonSize / 4;
        int fontSelectionButtonSize = (int) (addTextButtonSize * 1.5);
        int textSpacing = addTextButtonSize/8;

        int buttonHeight = 30;
        int heightSpacing = buttonHeight + 10;
        offset = imageHeight + buttonHeight;

        for( int i = 0; i < rotationModes.length; i++ ){
            JButton button = new JButton(rotationModes[i]);
            button.setBounds( 20 + (rotateSpacing*i) + (rotateButtonSize*(i)), offset, rotateButtonSize, buttonHeight );
            button.addActionListener( new RotateButtonListener( i ) );
            frame.add(button);
        } offset += heightSpacing;

        for( int i = 0; i < rotationAngles.length; i++ ){
            JButton button = new JButton(rotationAngles[i]);
            button.setBounds( 20 + (rotateAngleSpacing*i) + (rotateAngleButtonSize*(i)), offset, rotateAngleButtonSize, buttonHeight );
            button.addActionListener( new RotateAngleButtonListener(i) );
            frame.add(button);
        } offset += heightSpacing;

        for( int i = 0; i < flipOptions.length; i++ ){
            JButton button = new JButton(flipOptions[i]);
            button.setBounds( 20 + (flipSpacing*i) + (flipButtonSize*(i)), offset, flipButtonSize, buttonHeight );
            button.addActionListener( new FlipButtonListener( i ) );
            frame.add(button);
        } offset += heightSpacing;

        for( int i = 0; i < filters.length; i++ ){
            if( count != 0 && count % 3 == 0 ) { offset += buttonHeight + 10; count = 0; }
            JButton button = new JButton(filters[i]);
            button.setBounds( 20 + ( (filterSpacing*(i%3)) + (filterButtonSize*(i%3))), offset, filterButtonSize, buttonHeight );
            button.addActionListener( new FilterListener( i ) );
            frame.add(button);
            count++;
        } offset += heightSpacing;

        for( int i = 0; i < fileIO.length; i++ ){
            JButton button = new JButton(fileIO[i]);
            button.setBounds( 20, offset, imageWidth, buttonHeight );
            button.addActionListener(new IOButtonListener( i ));
            frame.add( button );
            offset += heightSpacing;
        }

        JButton addText = new JButton("add text");
        addText.setBounds( textboxX, offset, addTextButtonSize, buttonHeight );
        addText.addActionListener( new TextBoxButtonListener() );
        frame.add(addText);
        textboxX += (addTextButtonSize + textSpacing);

        JLabel lbl = new JLabel("font:");
        lbl.setBounds( textboxX, offset, selectionButtonSize, buttonHeight );
        frame.add(lbl);
        textboxX += (selectionButtonSize);

        JComboBox<String> fontChoices = new JComboBox<>(fontOptions);
        fontChoices.setBounds( textboxX, offset, fontSelectionButtonSize, buttonHeight );
        frame.add(fontChoices);
        fonts = fontChoices;
        textboxX += (fontSelectionButtonSize + textSpacing);

        JLabel lbl2 = new JLabel("sz:");
        lbl2.setBounds( textboxX, offset, selectionButtonSize, buttonHeight );
        frame.add(lbl2);
        textboxX += (selectionButtonSize);

        JTextField fontsz = new JTextField(1);
        fontsz.setBounds( textboxX, offset, selectionButtonSize, buttonHeight );
        frame.add(fontsz);
        fontSize = fontsz;
        textboxX += (selectionButtonSize + textSpacing);

        JLabel lbl3 = new JLabel("x:");
        lbl3.setBounds( textboxX, offset, selectionButtonSize, buttonHeight );
        frame.add(lbl3);
        textboxX += (selectionButtonSize);

        JTextField xCoord = new JTextField(1);
        xCoord.setBounds( textboxX, offset, selectionButtonSize, buttonHeight );
        frame.add(xCoord);
        textboxXCoor = xCoord;
        textboxX += (selectionButtonSize + textSpacing);

        JLabel lbl4 = new JLabel("y:");
        lbl4.setBounds( textboxX, offset, selectionButtonSize, buttonHeight );
        frame.add(lbl4);
        textboxX += (selectionButtonSize);

        JTextField yCoord = new JTextField(1);
        yCoord.setBounds( textboxX, offset, 50, buttonHeight );
        textboxYCoor = yCoord;
        frame.add(yCoord);
        offset += heightSpacing;

        JLabel lbl5 = new JLabel("text input:");
        lbl5.setBounds( 20, offset, 90, buttonHeight );
        frame.add(lbl5);

        JTextField textToAdd = new JTextField(1);
        textToAdd.setBounds( 120, offset, imageWidth - 100, buttonHeight );
        frame.add(textToAdd);
        textInput = textToAdd;
        offset += heightSpacing;

        JLabel lbl6 = new JLabel("brightness:");
        lbl6.setBounds( 20, offset, 90, buttonHeight );
        frame.add(lbl6);
        brightSlider = createSlider(offset, buttonHeight);

        frame.add(brightSlider);
    }

    /**
     * Initializes a slider for the JFrame.
     * @return -> A new JSlider.
     */
    public static JSlider createSlider( int offset, int buttonHeight ){
        JSlider brightnessSlider = new JSlider();
        brightnessSlider.setBounds( 120, offset, imageWidth - 100, buttonHeight );
        brightnessSlider.addMouseListener( new MyMouseListener() );
        brightnessSlider.setMinimum( -100 );
        brightnessSlider.setMaximum( 100 );
        brightnessSlider.setValue(0);
        brightnessSlider.addChangeListener( new BrightSliderButtonListener() );
        return brightnessSlider;
    }

    /**
     * This ActionListener is for changing rotation parameters.
     */
    private static class RotateButtonListener implements ActionListener {
        int action;
        public RotateButtonListener( int act ){ super(); action = act; }

        @Override public void actionPerformed(ActionEvent e) {
            if( action == 0 ) rotateMode = 1;
            if( action == 1 ) rotateMode = 2;
        }
    }

    /**
     * This ActionListener is for rotating NxN matrices uses an action listener to rotate it, based on input params
     */
    private static class RotateAngleButtonListener implements ActionListener {
        int action;
        public RotateAngleButtonListener( int act ){ super(); action = act; }

        @Override public void actionPerformed(ActionEvent e) {
            if( action == 0 ) ImageManipulationHandler.rotateImage( rotateMode, 90, imageGrid );
            if( action == 1 ) ImageManipulationHandler.rotateImage( rotateMode, 180, imageGrid );
            if( action == 2 ) ImageManipulationHandler.rotateImage( rotateMode, 270, imageGrid );
            frame.repaint();
        }
    }

    /**
     * This ActionListener is for flipping the matrix
     */
    private static class FlipButtonListener implements ActionListener {
        int action;
        public FlipButtonListener( int act ){ super(); action = act; }

        @Override public void actionPerformed(ActionEvent e) {
            if( action == 0 ) ImageManipulationHandler.flipHorizontal(imageGrid);
            if( action == 1 ) ImageManipulationHandler.flipVertical(imageGrid);
            frame.repaint();
        }
    }

    /**
     * This ActionListener is for applying filters to MxN matrices
     */
    private static class FilterListener implements ActionListener {
        int action;
        public  FilterListener( int act ){ super(); action = act; }

        @Override public void actionPerformed( ActionEvent e ){
            if( action == 0 ){ ImageManipulationHandler.greyScale(imageGrid); }
            if( action == 1 ){ ImageManipulationHandler.redScale(imageGrid); }
            if( action == 2 ){ ImageManipulationHandler.greenScale(imageGrid); }
            if( action == 3 ){ ImageManipulationHandler.blueScale(imageGrid); }
            if( action == 4 ){ ImageManipulationHandler.invert(imageGrid); }
            if( action == 5 ){ ImageManipulationHandler.setOriginal(imageGrid, originalImage ); }
            frame.repaint();
        }
    }

    /**
     * This ActionListener is for reading IO allows files to be read/output.
     */
    private static class IOButtonListener implements ActionListener {
        int action;
        public IOButtonListener( int act ){ super(); action = act; }
        @Override public void actionPerformed(ActionEvent e) {
            if( action == 0 ) {
                try {
                    int[][] fileImage = promptForImageFile();
                    imageWidth = fileImage.length;
                    imageHeight = fileImage[0].length;

                    imageGrid = new int[fileImage.length][fileImage[0].length];
                    originalImage = new int[fileImage.length][fileImage[0].length];
                    sliderImage = new int[fileImage.length][fileImage[0].length];

                    System.out.println("Changing arrays " + fileImage.length + " " + fileImage[0].length );

                    for (int i = 0; i < fileImage.length; i++) {
                        for (int j = 0; j < fileImage[0].length; j++) {
                            imageGrid[i][j] = fileImage[i][j];
                            originalImage[i][j] = fileImage[i][j];
                            sliderImage[i][j] = fileImage[i][j];
                        }
                    }

                    resetFrame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            if( action == 1 ) {
                try { FileIOHandler.writeImage(imageGrid, frame); }
                catch (IOException ioException) { ioException.printStackTrace(); }
            }
            frame.repaint();
        }
    }

    /**
     * This ActionListener is for applying textbox to an image, using the necessary parameters.
     */
    private static class TextBoxButtonListener implements ActionListener {
        public TextBoxButtonListener(){ super(); }

        @Override public void actionPerformed(ActionEvent e) {
            int x; int y; int fontsz; String textIn; String font;
            try{
                x = Integer.parseInt( textboxXCoor.getText() );
                y = Integer.parseInt( textboxYCoor.getText() );
                fontsz = Integer.parseInt( fontSize.getText() );
                font = fonts.getSelectedItem().toString();
                textIn = textInput.getText();
                ImageManipulationHandler.addTextbox( imageGrid, imageWidth, imageHeight, x, y, fontsz, font, textIn );
            } catch ( NumberFormatException exception ){ return; }
            frame.repaint();
        }
    }

    /**
     * This changeListener controls the brightness slider of the image manipulator.
     */
    private static class BrightSliderButtonListener implements ChangeListener {
        public BrightSliderButtonListener(){ super(); }

        @Override public void stateChanged(ChangeEvent e) {
            float val = brightSlider.getValue() / (float) 100;
            ImageManipulationHandler.brightnessEdit( imageGrid, sliderImage, imageWidth, imageHeight, val );
            frame.repaint();
        }
    }

    /**
     * This saves the image before applying brightness, so only one brightness change is applied at the end,
     * and the preview can still be shown without causing changes until release.
     */
    private static class MyMouseListener implements MouseListener {
        public MyMouseListener(){ super(); }
        @Override public void mouseClicked(MouseEvent e) { }
        @Override public void mouseEntered(MouseEvent e) { }
        @Override public void mouseExited(MouseEvent e) { }

        @Override public void mouseReleased(MouseEvent e) {
            for (int i = 0; i < imageGrid.length; i++)
                sliderImage[i] = Arrays.copyOf(Main.getImageArray()[i], Main.getImageArray()[i].length);
            brightSlider.setValue(0);
        }

        @Override public void mousePressed(MouseEvent e) {
            for (int i = 0; i < imageGrid.length; i++)
                sliderImage[i] = Arrays.copyOf(Main.getImageArray()[i], Main.getImageArray()[i].length);
        }
    }
}

/**
 * This class holds an image panel that extends JPanel, to paint the image on.
 * Allows me to only repaint image and not the buttons on image manipulation
 */
class ImagePanel extends JPanel{
    public void paint( Graphics g ){
        Image image = ImageManipulationHandler.arrayToImage( Main.getImageArray() );
        g.drawImage( image, 20, 20, this );
    }
}