package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Hamming {
	
	@FXML private TextField entryBits;
	@FXML private TextField errorBitPosition;
	@FXML private TextField gatesEntryBits;
	@FXML private Label generatedCode;
	@FXML private Label correctedCode;
	@FXML private Label errorCode;
	@FXML private Label entryMessageLabel;
	@FXML private Label errorMessageLabel;
	@FXML private Button correctCodeButton;
    @FXML private Button andButton;
    @FXML private Button orButton;
    //@FXML private Button xorButton;
	@FXML private MenuItem pdfOutputOption;
	@FXML private CheckBox randomErrorBitBox;
	
	private int a[], b[];
	private Random generator = new Random();
	private static boolean usingFileChooser = false;
		
	private void initializeHamming() {

		a = new int[entryBits.getText().length()];
				
	    for (int i = 0; i < entryBits.getText().length(); i++) {
	        a[entryBits.getText().length() - i - 1] = entryBits.getText().charAt(i) - '0';
	    }		
		b = Utils.generateCode(a);
	}

	private void resetGUI() {
		Platform.runLater(() -> {
            entryMessageLabel.setTextAlignment(TextAlignment.CENTER);
            entryMessageLabel.setText("nie wprowadzono danych lub wprowadzono wartości różne od bitów");
            entryMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14pt;");
            errorCode.setText("");
            correctedCode.setText("");
            generatedCode.setText("");
            errorBitPosition.setText("");
            errorMessageLabel.setText("");
            pdfOutputOption.setDisable(true);
            correctCodeButton.setDisable(true);
        });
	}
	
	private void handleRandomingCheckBox() {
		if (randomErrorBitBox.isSelected()) {
			Platform.runLater(() -> {
                String randomErrorBit = Integer.toString(generator.nextInt(entryBits.getText().length()) + 1);
                errorBitPosition.setText(randomErrorBit);
                fixError();
            });
		}
		else { 
			errorBitPosition.setText("");
			pdfOutputOption.setDisable(true);
		}
	}
	
	@FXML public void doHamming() {

		if (entryBits.getText().matches("[0-1]+") && !entryBits.getText().equals("")) {
			Platform.runLater(() -> {
                StringBuilder generated = new StringBuilder();
                initializeHamming();

                for (int i = 0 ; i < b.length ; i++) {
                    generated.append(Integer.toString(b[b.length - i - 1]));
                }

                entryMessageLabel.setText("");
                errorMessageLabel.setText("");
                errorCode.setText("");
                correctedCode.setText("");
                generatedCode.setText(generated.toString());
                correctCodeButton.setDisable(false);
                if (!usingFileChooser) handleRandomingCheckBox();
            });
		}
		else resetGUI();
	}
	
	@FXML public void fixError() {
			// Difference in the sizes of original and new array gives the number of parity bits added.

		Platform.runLater(() -> {

            if (errorBitPosition.getText().matches("[0-9]+") && (!errorBitPosition.getText().equals(""))) {

                int errorBit = Integer.parseInt(errorBitPosition.getText());

                if ((errorBit != 0) && (Integer.parseInt(errorBitPosition.getText()) <= generatedCode.getText().length())) {

                    pdfOutputOption.setDisable(false);
                    StringBuilder error = new StringBuilder();
					initializeHamming();
					b[errorBit - 1] = (b[errorBit - 1] + 1)%2;

                    for(int i = 0 ; i < b.length ; i++) {
                        error.append(Integer.toString(b[b.length - i - 1]));
                    }
                    errorCode.setText(error.toString());
                    String corrected = Utils.receive(b, b.length - a.length);
                    correctedCode.setText(corrected);
                    errorMessageLabel.setText("");
                    usingFileChooser = false;
                }
				else {
                    errorMessageLabel.setText("wprowadzono niepoprawną lokalizację");
                    errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16pt;");
                    errorCode.setText("");
                    correctedCode.setText("");
                    pdfOutputOption.setDisable(true);
                }
            }
            else {
                errorMessageLabel.setTextAlignment(TextAlignment.CENTER);
                errorMessageLabel.setText("nie wprowadzono danych lub wprowadzono wartości różne od liczb");
                errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14pt;");
                errorCode.setText("");
                correctedCode.setText("");
                pdfOutputOption.setDisable(true);
            }
        });
	}
	
	@FXML public void handleFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open .txt File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		File file = fileChooser.showOpenDialog(MainApp.primaryStage);
		
		if (file != null) {
			usingFileChooser = true;
			Scanner scanner;
			try {
				scanner = new Scanner(file);
				entryMessageLabel.setText("");
				errorCode.setText("");
				correctedCode.setText("");
				errorMessageLabel.setText("");
				entryBits.setText(scanner.nextLine().replaceAll("\\s",""));
				doHamming();
				Platform.runLater(() -> {
                    errorBitPosition.setText(scanner.nextLine());
                    System.out.println(errorBitPosition.getText());
                    scanner.close();
                });
				fixError();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML public void randomValuesButton() {
		String randomEntryBits = Integer.toBinaryString(generator.nextInt(2000) + 100);
		entryBits.setText(randomEntryBits);
		doHamming();
		handleRandomingCheckBox();
	}
	
	@FXML public void createPDF() {
        Document document = new Document();
		FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("pdf Files", "*.pdf"));
        fileChooser.setTitle("Save pdf file");
        fileChooser.setInitialFileName("output.pdf");
        File savedFile = fileChooser.showSaveDialog(MainApp.primaryStage);
        
        if (savedFile != null) {
	        try {
				PdfWriter.getInstance(document, new FileOutputStream(savedFile));
			} catch (DocumentException | IOException e) {
				e.printStackTrace();
			}
	        document.open();
	        
	        try {
	            BaseFont baseFont = BaseFont.createFont("fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	            Font newFont = new Font(baseFont, 12);
				document.add(new Paragraph("Wygenerowany kod: " + generatedCode.getText()));
				document.add(new Paragraph("Bit błędu: " + errorBitPosition.getText(), newFont));
				document.add(new Paragraph("Kod z błędem: " + errorCode.getText(), newFont));
				document.add(new Paragraph("Poprawiony kod: " + correctedCode.getText()));
			} catch (DocumentException | IOException e) {
				e.printStackTrace();
			}
	        document.close();
        }
	}

	@FXML public void handleGates() {
		if (gatesEntryBits.getText().matches("[0-1]+") && !gatesEntryBits.getText().equals("")) {
			Platform.runLater(() -> {
                String input = gatesEntryBits.getText();
                StringBuilder generated = new StringBuilder(30);

                for (int i = 0; i < gatesEntryBits.getText().length(); i++) {
                    int bit = (generator.nextBoolean()) ? 1 : 0;
                    generated.append(bit);
                }
                String randomBits = generated.toString();
				int result;

				if (andButton.isPressed()) {
                    result = (Integer.parseInt(input, 2)) & (Integer.parseInt(randomBits, 2));
                }
                else if (orButton.isPressed()) {
                    result = (Integer.parseInt(input, 2)) | (Integer.parseInt(randomBits, 2));
                }
                else result = (Integer.parseInt(input, 2)) ^ (Integer.parseInt(randomBits, 2));

				entryBits.setText(Integer.toBinaryString(result));
				doHamming();
				handleRandomingCheckBox();
			});
		}
		else resetGUI();
	}
}